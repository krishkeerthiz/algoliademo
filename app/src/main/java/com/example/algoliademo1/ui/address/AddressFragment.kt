package com.example.algoliademo1.ui.address

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.algoliademo1.R
import com.example.algoliademo1.data.source.local.entity.Address
import com.example.algoliademo1.databinding.FragmentAddressBinding
import com.example.algoliademo1.ui.MainActivity
import com.google.android.material.textfield.TextInputEditText

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

                val spinner = binding.addressSpinner as Spinner
                spinner.onItemSelectedListener

                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, addressList)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                binding.addressSpinner.adapter = adapter

                binding.addressSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                    Toast.makeText(requireContext(), addressList[position], Toast.LENGTH_SHORT).show()

                    viewModel.addressId = addresses[position].addressId
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
                }
            }
        }

        binding.placeOrderButton.setOnClickListener {
            viewModel.placeOrder()

            Toast.makeText(requireContext(), "Order placed successfully", Toast.LENGTH_SHORT).show()
            (requireActivity() as MainActivity).showOrdersFragment()

        }

        binding.addAddressButton.setOnClickListener {
            showAddAddressDialog()
        }

    }



    private fun showAddAddressDialog(){
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Add Address")

        val addAddressDialog = layoutInflater.inflate(R.layout.add_address_dialog, null)
        builder.setView(addAddressDialog)

        val doorNumberEditText = addAddressDialog.findViewById<TextInputEditText>(R.id.doorNumberText)
        val addressEditText = addAddressDialog.findViewById<TextInputEditText>(R.id.addressText)
        val cityEditText = addAddressDialog.findViewById<TextInputEditText>(R.id.cityText)
        val pincodeEditText = addAddressDialog.findViewById<TextInputEditText>(R.id.pincodeText)
        val stateEditText = addAddressDialog.findViewById<TextInputEditText>(R.id.stateText)

        builder.setPositiveButton("Save"){ dialogInterface, i ->
            viewModel.addAddress(
                doorNumberEditText.text.toString(),
                addressEditText.text.toString(),
                cityEditText.text.toString(),
                pincodeEditText.text.toString().toInt(),
                stateEditText.text.toString(),
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
                dialog.getButton(Dialog.BUTTON_POSITIVE).isEnabled = !(doorNumberEditText.text.toString().isEmpty() ||
                        addressEditText.text.toString().isEmpty() || cityEditText.text.toString().isEmpty() ||
                        pincodeEditText.text.toString().isEmpty() || stateEditText.text.toString().isEmpty() )
            }
        }

        doorNumberEditText.addTextChangedListener(watcher)
        addressEditText.addTextChangedListener(watcher)
        cityEditText.addTextChangedListener(watcher)
        pincodeEditText.addTextChangedListener(watcher)
        stateEditText.addTextChangedListener(watcher)

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
}