package com.example.algoliademo1.ui.address

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.algoliademo1.R
import com.example.algoliademo1.data.source.local.entity.Order
import com.example.algoliademo1.databinding.FragmentAddressBinding
import com.example.algoliademo1.model.PincodeDetail
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
        return inflater.inflate(R.layout.fragment_address, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAddressBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[AddressViewModel::class.java]

        viewModel.getAddresses()

        viewModel.addresses.observe(viewLifecycleOwner) { addresses ->
            if (addresses != null) {
                val addressList = viewModel.getAddressList(addresses)

                if (addressList.isEmpty()) {
                    binding.emptyLayout.visibility = View.VISIBLE
                    binding.placeOrderButton.visibility = View.GONE
                    binding.addressTextView.text = resources.getString(R.string.add_address_first)
                    binding.addressSpinner.visibility = View.INVISIBLE
                } else {
                    binding.placeOrderButton.visibility = View.VISIBLE
                    binding.addressSpinner.visibility = View.VISIBLE
                    binding.addressTextView.text = resources.getString(R.string.choose_delivery_address)
                }

                val spinner = binding.addressSpinner

                val adapter = ArrayAdapter(requireContext(), R.layout.state_dropdown, addressList)
                adapter.setDropDownViewResource(R.layout.state_dropdown)

                spinner.adapter = adapter

                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        p0: AdapterView<*>?,
                        p1: View?,
                        position: Int,
                        p3: Long
                    ) {
                        binding.placeOrderButton.isClickable = true
                        Log.d(TAG, "onItemSelected: ${addresses[position].addressId}")
                        viewModel.addressId = addresses[position].addressId
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                    }
                }
            }
        }

        binding.placeOrderButton.setOnClickListener {
            showAlertDialog()
        }

        binding.addAddressButton.setOnClickListener {
            showPincodeDialog()
        }

    }

    private fun showAlertDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext()).apply {
            setTitle("Confirm order")
            setMessage("Do you want to place order?")
            setPositiveButton("Yes") { _, _ ->
                lifecycleScope.launch(IO) {
                    Log.d(TAG, "showAlertDialog: before placing order")
                    val order = viewModel.placeOrder()

                    Log.d(TAG, "order total: ${order.total}")
                    withContext(Main) {
                        Log.d(TAG, "showAlertDialog: ")
                        Toast.makeText(
                            requireContext(),
                            "Order placed successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        gotoOrderDetailFragment(order)
                    }
                }
            }
            setNegativeButton("No") { dialogInterface, _ ->
                dialogInterface.cancel()
            }
            setCancelable(true)
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()

    }

    private fun showPincodeDialog() {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Enter Pincode")

        val pincodeDialog = layoutInflater.inflate(R.layout.add_pincode_dialog, null)
        builder.setView(pincodeDialog).setCancelable(false)

        val pincodeEditText = pincodeDialog.findViewById<TextInputEditText>(R.id.pincodeText)
        val pincodeLayout =
            pincodeDialog.findViewById<TextInputLayout>(R.id.outlinedPincodeTextField)

        builder.setPositiveButton("Save") { dialogInterface, _ ->
            binding.loadingPanel.visibility = View.VISIBLE
            turnOffButtons()

            viewModel.getPincodeDetails(pincodeEditText.text.toString())

            viewModel.pincodeModel.observe(viewLifecycleOwner) { pincodeList ->
                if (pincodeList != null) {
                    binding.loadingPanel.visibility = View.INVISIBLE


                    if (pincodeList[0].postOffice == null) {
                        Toast.makeText(
                            requireContext(),
                            "Pincode not registered",
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.pincodeModel.value = null
                        dialogInterface.dismiss()
                        showPincodeDialog()
                    } else {
                        turnOnButttons()
                        val postOffice = pincodeList[0].postOffice!![0]
                        viewModel.pincodeDetail = PincodeDetail(
                            postOffice.name + ", " + postOffice.district,
                            postOffice.state!!, postOffice.pincode!!.toInt()
                        )

                        viewModel.pincodeModel.value = null
                        dialogInterface.dismiss()
                        showAddAddressDialog()
                    }

                }
            }
        }

        builder.setNegativeButton("Cancel") { dialogInterface, _ ->
            turnOnButttons()
            dialogInterface.dismiss()
        }

        val dialog = builder.create()
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false

        val pincodeWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val pincode = pincodeEditText.text.toString()

                if (pincode.isEmpty() || pincode.length != 6 || pincode.toInt() < 100000)
                    pincodeLayout.error = "Enter valid pincode"
                else {
                    pincodeLayout.error = null
                    pincodeLayout.isErrorEnabled = false
                }

                dialog.getButton(Dialog.BUTTON_POSITIVE).isEnabled = !(pincode.isEmpty() ||
                        (pincode.length != 6) || (pincode.toInt() < 100000))
            }
        }
        pincodeEditText.addTextChangedListener(pincodeWatcher)

    }

    private fun showAddAddressDialog() {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Add New Address")

        val addAddressDialog = layoutInflater.inflate(R.layout.add_address_dialog, null)
        builder.setView(addAddressDialog).setCancelable(false)

        val doorNumberEditText =
            addAddressDialog.findViewById<TextInputEditText>(R.id.doorNumberText)
        val streetEditText = addAddressDialog.findViewById<TextInputEditText>(R.id.streetText)
        val cityText = addAddressDialog.findViewById<TextView>(R.id.cityText)
        val stateText = addAddressDialog.findViewById<TextView>(R.id.stateText)
        val pincodeText = addAddressDialog.findViewById<TextView>(R.id.pincodeText)

        val doorNumberLayout =
            addAddressDialog.findViewById<TextInputLayout>(R.id.outlinedDoorNumberTextField)
        val streetLayout =
            addAddressDialog.findViewById<TextInputLayout>(R.id.outlinedStreetTextField)

        cityText.text = viewModel.pincodeDetail?.city
        stateText.text = viewModel.pincodeDetail?.state
        pincodeText.text = viewModel.pincodeDetail?.pincode.toString()

        cityText.isClickable = false
        cityText.isCursorVisible = false
        cityText.isFocusable = false

        stateText.isClickable = false
        stateText.isCursorVisible = false
        stateText.isFocusable = false

        pincodeText.isClickable = false
        pincodeText.isCursorVisible = false
        pincodeText.isFocusable = false

        builder.setPositiveButton("Save") { dialogInterface, _ ->
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

        builder.setNegativeButton("Cancel") { dialogInterface, _ ->
            viewModel.pincodeModel.value = null
            viewModel.pincodeDetail = null
            dialogInterface.dismiss()
        }

        val dialog = builder.create()
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false

        val doorNumberWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (doorNumberCheck(doorNumberEditText.text?.trim().toString()))
                    doorNumberLayout.error = "Enter valid door number"
                else {
                    doorNumberLayout.error = null
                    doorNumberLayout.isErrorEnabled = false
                }
                dialog.getButton(Dialog.BUTTON_POSITIVE).isEnabled =
                    !(doorNumberCheck(doorNumberEditText.text?.trim().toString()) ||
                            streetCheck(streetEditText.text?.trim().toString()))
            }
        }

        val streetWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (streetCheck(streetEditText.text?.trim().toString()))
                    streetLayout.error = "Enter valid street name"
                else {
                    streetLayout.error = null
                    streetLayout.isErrorEnabled = false
                }

                dialog.getButton(Dialog.BUTTON_POSITIVE).isEnabled =
                    !(doorNumberCheck(doorNumberEditText.text?.trim().toString()) ||
                            streetCheck(streetEditText.text?.trim().toString()))
            }

        }


        doorNumberEditText.addTextChangedListener(doorNumberWatcher)
        streetEditText.addTextChangedListener(streetWatcher)

    }

    private fun gotoOrderDetailFragment(order: Order) {
        val action = AddressFragmentDirections.actionAddressFragmentToOrderDetailFragment(order)
        view?.findNavController()?.navigate(action)
    }

    private fun turnOffButtons() {
        binding.placeOrderButton.isClickable = false
        binding.addAddressButton.isClickable = false
    }

    private fun turnOnButttons() {
        binding.placeOrderButton.isClickable = true
        binding.addAddressButton.isClickable = true
    }
}