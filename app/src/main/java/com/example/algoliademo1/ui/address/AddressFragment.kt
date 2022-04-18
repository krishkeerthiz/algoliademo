package com.example.algoliademo1.ui.address

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.algoliademo1.R
import com.example.algoliademo1.data.source.local.entity.Order
import com.example.algoliademo1.databinding.FragmentAddressBinding
import com.example.algoliademo1.model.PincodeDetail
import com.example.algoliademo1.model.PincodeModel
import com.example.algoliademo1.util.cityCheck
import com.example.algoliademo1.util.doorNumberCheck
import com.example.algoliademo1.util.streetCheck
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddressFragment : Fragment() {
    private lateinit var binding: FragmentAddressBinding
    private lateinit var viewModel: AddressViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_address, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAddressBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[AddressViewModel::class.java]

        viewModel.getAddresses()

        viewModel.addresses.observe(viewLifecycleOwner){ addresses ->
            if(addresses != null){
                val addressList = viewModel.getAddressList(addresses)

                if(addressList.isEmpty()){
                    binding.emptyLayout.visibility = View.VISIBLE
                    binding.placeOrderButton.visibility = View.GONE
                   // binding.placeOrderButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green_greyed))
                    binding.addressTextView.text = "Add Address First"
                    binding.addressSpinner.visibility = View.INVISIBLE
                    //binding.outlinedAddressTextField.visibility = View.GONE
                    //Toast.makeText(requireContext(), "Add address to place order", Toast.LENGTH_SHORT).show()
                }
                else {
                    //binding.emptyLayout.visibility = View.INVISIBLE
                    binding.placeOrderButton.visibility = View.VISIBLE
                 //   binding.placeOrderButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
                    binding.addressSpinner.visibility = View.VISIBLE
                    //binding.outlinedAddressTextField.visibility = View.VISIBLE
                    binding.addressTextView.text = "Choose Delivery Address"
                }
//                val spinner = binding.addressSpinner as Spinner
//                spinner.onItemSelectedListener

                val spinner = binding.addressSpinner
                //spinner.onItemSelectedListener

                val adapter = ArrayAdapter(requireContext(), R.layout.state_dropdown, addressList)
                adapter.setDropDownViewResource(R.layout.state_dropdown)

                spinner.adapter = adapter

                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                    binding.placeOrderButton.isClickable = true
                    //Toast.makeText(requireContext(), "${addresses[position].addressId}", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "onItemSelected: ${addresses[position].addressId}")
                    viewModel.addressId = addresses[position].addressId
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
                }
            }
        }

        binding.placeOrderButton.setOnClickListener {
//            if(binding.addressDropdown.text.toString().isEmpty())
//                Toast.makeText(requireContext(), "Select address to place order", Toast.LENGTH_SHORT).show()
//            else
                showAlertDialog()
        }

        binding.addAddressButton.setOnClickListener {
            showPincodeDialog()
        }

    }

    private fun showAlertDialog(){
        val alertDialogBuilder = AlertDialog.Builder(requireContext()).apply {
            setTitle("Confirm order")
            setMessage("Do you want to place order?")
            setPositiveButton("Yes"){ dialogInterface, i ->
                lifecycleScope.launch(IO) {
                    Log.d(TAG, "showAlertDialog: before placing order")
                    val order = viewModel.placeOrder()

                    Log.d(TAG, "order total: ${order.total}")
                    withContext(Main) {
                        Log.d(TAG, "showAlertDialog: ")
                        Toast.makeText(requireContext(), "Order placed successfully", Toast.LENGTH_SHORT).show()
                        gotoOrderDetailFragment(order)
                    }
                }
            }
            setNegativeButton("No"){ dialogInterface, i ->
                dialogInterface.cancel()
            }
            setCancelable(true)
        }

        val alertDialog =alertDialogBuilder.create()
        alertDialog.show()

    }

    private fun showPincodeDialog(){
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Enter Pincode")

        val pincodeDialog = layoutInflater.inflate(R.layout.add_pincode_dialog, null)
        builder.setView(pincodeDialog).setCancelable(false)

        val pincodeEditText = pincodeDialog.findViewById<TextInputEditText>(R.id.pincodeText)
        val pincodeLayout = pincodeDialog.findViewById<TextInputLayout>(R.id.outlinedPincodeTextField)

        builder.setPositiveButton("Save"){ dialogInterface, i ->
            binding.loadingPanel.visibility = View.VISIBLE
            turnOffButtons()

            viewModel.getPincodeDetails(pincodeEditText.text.toString())

            viewModel.pincodeModel.observe(viewLifecycleOwner){pincodeList ->
                if(pincodeList != null){
                    binding.loadingPanel.visibility = View.INVISIBLE


                    if(pincodeList[0].postOffice == null){
                        Toast.makeText(requireContext(), "Pincode not registered", Toast.LENGTH_SHORT).show()
                        viewModel.pincodeModel.value = null
                        dialogInterface.dismiss()
                        showPincodeDialog()
                    }
                    else{
                        turnOnButttons()
                        val postOffice = pincodeList[0].postOffice!![0]
                        viewModel.pincodeDetail = PincodeDetail(postOffice.name + ", "+ postOffice.district,
                            postOffice.state!!, postOffice.pincode!!.toInt())

                        viewModel.pincodeModel.value = null
                        dialogInterface.dismiss()
                        showAddAddressDialog()
                    }

                }
            }
        }

        builder.setNegativeButton("Cancel"){ dialogInterface, i ->
            turnOnButttons()
            dialogInterface.dismiss()
        }

        val dialog = builder.create()
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false

        val pincodeWatcher = object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                var pincode = pincodeEditText.text.toString()

                if(pincode.isEmpty() || pincode.length != 6 || pincode.toInt() < 100000)
                    pincodeLayout.error = "Enter valid pincode"
                else{
                    pincodeLayout.error = null
                    pincodeLayout.isErrorEnabled = false
                }

                dialog.getButton(Dialog.BUTTON_POSITIVE).isEnabled = !(pincode.isEmpty()  ||
                        (pincode.length != 6 ) || (pincode.toInt() < 100000))
            }
        }
        pincodeEditText.addTextChangedListener(pincodeWatcher)

    }

    private fun showAddAddressDialog(){
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Add New Address")

        val addAddressDialog = layoutInflater.inflate(R.layout.add_address_dialog, null)
        builder.setView(addAddressDialog).setCancelable(false)

        val doorNumberEditText = addAddressDialog.findViewById<TextInputEditText>(R.id.doorNumberText)
        val streetEditText = addAddressDialog.findViewById<TextInputEditText>(R.id.streetText)
        val cityText = addAddressDialog.findViewById<TextView>(R.id.cityText)
        val stateText = addAddressDialog.findViewById<TextView>(R.id.stateText)
        val pincodeText = addAddressDialog.findViewById<TextView>(R.id.pincodeText)

        val doorNumberLayout = addAddressDialog.findViewById<TextInputLayout>(R.id.outlinedDoorNumberTextField)
        val streetLayout = addAddressDialog.findViewById<TextInputLayout>(R.id.outlinedStreetTextField)
        //val cityLayout = addAddressDialog.findViewById<TextInputLayout>(R.id.outlinedCityTextField)
//        val states = resources.getStringArray(R.array.states)
//        val statesAdapter = ArrayAdapter(requireContext(), R.layout.state_dropdown, states )
//        stateDropdown.setAdapter(statesAdapter)

        cityText.setText(viewModel.pincodeDetail?.city)
        stateText.setText(viewModel.pincodeDetail?.state)
        pincodeText.setText(viewModel.pincodeDetail?.pincode.toString())

        cityText.isClickable = false
        cityText.isCursorVisible = false
        cityText.isFocusable = false

        stateText.isClickable = false
        stateText.isCursorVisible = false
        stateText.isFocusable = false

        pincodeText.isClickable = false
        pincodeText.isCursorVisible = false
        pincodeText.isFocusable = false

        builder.setPositiveButton("Save"){ dialogInterface, i ->
            viewModel.addAddress(
                doorNumberEditText.text.toString(),
                streetEditText.text.toString(),
                cityText.text.toString(),
                pincodeText.text.toString().toInt(),
                stateText.text.toString()
            )
            viewModel.pincodeModel.value = null
            viewModel.pincodeDetail = null
            dialogInterface.dismiss()
        }

        builder.setNegativeButton("Cancel"){ dialogInterface, i ->
            viewModel.pincodeModel.value = null
            viewModel.pincodeDetail = null
            dialogInterface.dismiss()
        }


        // load pincode
//        loadPincodeDetails.setOnClickListener {
//            Log.d(TAG, "showAddAddressDialog: inside add address")
//            viewModel.getPincodeDetails("600020")
//
//        }
        val dialog = builder.create()
        dialog.show()

        // Pincode details fetching
//        viewModel.pincodeModel.observe(viewLifecycleOwner){ pincodeModel ->
//            Log.d(TAG, "showAddAddressDialog: inside pincode observer")
//            if(pincodeModel != null){
//                Log.d(TAG, "showAddAddressDialog: insise valid pincode ")
//                Toast.makeText(requireContext(), pincodeModel.toString(), Toast.LENGTH_SHORT).show()
//
//                cityEditText.setText(pincodeModel[0].postOffice[0].district.toString())
//                stateDropdown.setText(pincodeModel[0].postOffice[0].state.toString())
//
//                viewModel.pincodeModel.value = null
//            }
//        }

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false

        val doorNumberWatcher = object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                if(doorNumberCheck(doorNumberEditText.text?.trim().toString()))
                    doorNumberLayout.error = "Enter valid door number"
                else{
                    doorNumberLayout.error = null
                    doorNumberLayout.isErrorEnabled = false
                }
                dialog.getButton(Dialog.BUTTON_POSITIVE).isEnabled = !( doorNumberCheck(doorNumberEditText.text?.trim().toString()) ||
                        streetCheck(streetEditText.text?.trim().toString()))
            }
        }

        val streetWatcher = object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                if(streetCheck(streetEditText.text?.trim().toString()))
                    streetLayout.error = "Enter valid street name"
                else{
                    streetLayout.error = null
                    streetLayout.isErrorEnabled = false
                }

                dialog.getButton(Dialog.BUTTON_POSITIVE).isEnabled = !( doorNumberCheck(doorNumberEditText.text?.trim().toString()) ||
                        streetCheck(streetEditText.text?.trim().toString()))
            }

        }


        doorNumberEditText.addTextChangedListener(doorNumberWatcher)
        streetEditText.addTextChangedListener(streetWatcher)
//        cityEditText.addTextChangedListener(cityWatcher)
//        pincodeEditText.addTextChangedListener(pincodeWatcher)
        //stateDropdown.addTextChangedListener(stateWatcher)

//    }if(addresses != null){
//        val addressList = addresses.values.toList()
//
//            val spinner = binding.addressSpinner as Spinner
//            spinner.setOnItemselectedListener(this)
//
//        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, addressList)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//
//        binding.addressSpinner.adapter = adapter
//
//        binding.addressSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
//            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
//                Toast.makeText(requireContext(), addressList[position], Toast.LENGTH_SHORT).show()
//
//                viewModel.addressId = addresses.filterValues{ it == addressList[position] }.keys.first()
//            }
//            override fun onNothingSelected(p0: AdapterView<*>?) {
//            }
//
//        }
//    }
        }

    private fun gotoOrderDetailFragment(order: Order) {
        val action = AddressFragmentDirections.actionAddressFragmentToOrderDetailFragment(order)
        view?.findNavController()?.navigate(action)
    }

    private fun turnOffButtons(){
        binding.placeOrderButton.isClickable = false
        binding.addAddressButton.isClickable = false
    }

    private fun turnOnButttons(){
        binding.placeOrderButton.isClickable = true
        binding.addAddressButton.isClickable = true
    }
}