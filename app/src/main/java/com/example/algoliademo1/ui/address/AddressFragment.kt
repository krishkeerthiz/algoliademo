package com.example.algoliademo1.ui.address

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.algoliademo1.R
import com.example.algoliademo1.data.source.local.entity.Order
import com.example.algoliademo1.databinding.FragmentAddressBinding
import com.google.android.material.textfield.TextInputEditText
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
                    binding.placeOrderButton.isClickable = false
                    binding.addressTextView.text = "Add Address First"
                    binding.outlinedAddressTextField.visibility = View.GONE
                    //Toast.makeText(requireContext(), "Add address to place order", Toast.LENGTH_SHORT).show()
                }
                else {
                    //binding.emptyLayout.visibility = View.INVISIBLE
                    binding.placeOrderButton.isClickable = true
                    binding.outlinedAddressTextField.visibility = View.VISIBLE
                    binding.addressTextView.text = "Choose Delivery Address"
                }
//                val spinner = binding.addressSpinner as Spinner
//                spinner.onItemSelectedListener

                val spinner = binding.addressDropdown
                spinner.onItemSelectedListener

                val adapter = ArrayAdapter(requireContext(), R.layout.state_dropdown, addressList)
                //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                spinner.setAdapter(adapter)

                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                    binding.placeOrderButton.isClickable = true
                    //Toast.makeText(requireContext(), addressList[position], Toast.LENGTH_SHORT).show()
                    viewModel.addressId = addresses[position].addressId
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
                }
            }
        }

        binding.placeOrderButton.setOnClickListener {
            if(binding.addressDropdown.text.toString().isEmpty())
                Toast.makeText(requireContext(), "Select address to place order", Toast.LENGTH_SHORT).show()
            else
                showAlertDialog()
        }

        binding.addAddressButton.setOnClickListener {
            showAddAddressDialog()
        }

    }

    private fun showAlertDialog(){
        val alertDialogBuilder = AlertDialog.Builder(requireContext()).apply {
            setTitle("Confirm order")
            setMessage("Do you want to place order?")
            setPositiveButton("Yes"){ dialogInterface, i ->
                lifecycleScope.launch(IO) {
                    val order = viewModel.placeOrder()

                    Log.d(TAG, "order total: ${order.total}")
                    withContext(Main) {
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


    private fun showAddAddressDialog(){
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Add New Address")

        val addAddressDialog = layoutInflater.inflate(R.layout.add_address_dialog, null)
        builder.setView(addAddressDialog).setCancelable(false)

        val doorNumberEditText = addAddressDialog.findViewById<TextInputEditText>(R.id.doorNumberText)
        val addressEditText = addAddressDialog.findViewById<TextInputEditText>(R.id.addressText)
        val cityEditText = addAddressDialog.findViewById<TextInputEditText>(R.id.cityText)
        val pincodeEditText = addAddressDialog.findViewById<TextInputEditText>(R.id.pincodeText)
        val stateDropdown = addAddressDialog.findViewById<AutoCompleteTextView>(R.id.stateDropdown)

        val states = resources.getStringArray(R.array.states)
        val statesAdapter = ArrayAdapter(requireContext(), R.layout.state_dropdown, states )
        stateDropdown.setAdapter(statesAdapter)

        builder.setPositiveButton("Save"){ dialogInterface, i ->
            viewModel.addAddress(
                doorNumberEditText.text.toString(),
                addressEditText.text.toString(),
                cityEditText.text.toString(),
                pincodeEditText.text.toString().toInt(),
                stateDropdown.text.toString(),
            )
        }

        builder.setNegativeButton("Cancel"){ dialogInterface, i ->
            dialogInterface.cancel()
        }

        val dialog = builder.create()
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false

        val watcher = object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                dialog.getButton(Dialog.BUTTON_POSITIVE).isEnabled = !(doorNumberEditText.text?.trim().toString().isEmpty() ||
                        addressEditText.text?.trim().toString().isEmpty() || cityEditText.text?.trim().toString().isEmpty() ||
                        pincodeEditText.text.toString().isEmpty()  || stateDropdown.text.toString().isEmpty() ||
                        (pincodeEditText.text.toString().length != 6 ) || (pincodeEditText.text.toString().toInt() < 100000))
            }
        }

        doorNumberEditText.addTextChangedListener(watcher)
        addressEditText.addTextChangedListener(watcher)
        cityEditText.addTextChangedListener(watcher)
        pincodeEditText.addTextChangedListener(watcher)
       // stateEditText.addTextChangedListener(watcher)

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
}