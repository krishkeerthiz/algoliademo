package com.example.algoliademo1.ui.cart

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.algoliademo1.*
import com.example.algoliademo1.databinding.FragmentCartBinding
import com.example.algoliademo1.ui.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartFragment : Fragment() {
    private lateinit var viewModel: CartViewModel
    private lateinit var binding: FragmentCartBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(requireActivity())[CartViewModel::class.java]

        viewModel.getCartItems()
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCartBinding.bind(view)

        // setTotalPriceView()


        val cartAdapter = CartAdapter(CartOnClickListener(
            { productId -> gotoProductDetailFragment(productId) },//(requireActivity() as MainActivity).showProductDetailFragment(productId)},
            { productId, price ->
                showAlertDialog(productId, price)
            },
            { productId ->
                viewModel.incrementItemAndUpdate(productId)
            },
            { productId ->
                viewModel.decrementItemAndUpdate(productId)
            }
        ))

        viewModel.cartModel.observe(viewLifecycleOwner) {
            if (it != null) {
                // binding.buyButton.isClickable = it.products?.size != 0
                if (it.products?.size == 0) {
                    binding.emptyLayout.visibility = View.VISIBLE
                    binding.cartLayout.visibility = View.INVISIBLE
                } else {
                    binding.emptyLayout.visibility = View.INVISIBLE
                    binding.cartLayout.visibility = View.VISIBLE
                }

                cartAdapter.submitList(it.products?.keys?.toList())
                cartAdapter.notifyDataSetChanged()
                binding.totalPrice.text = getString(R.string.currency) + String.format("%.2f", it.total)
                //Toast.makeText(requireContext(), it.products?.entries.toString(), Toast.LENGTH_LONG).show()
            }
        }

        binding.cartItemsList.let {
            it.itemAnimator = null
            it.adapter = cartAdapter
            it.layoutManager = LinearLayoutManager(requireContext())
        }

        binding.buyButton.setOnClickListener {
            gotoAddressFragment()
            //(requireActivity() as MainActivity).showAddressFragment()
        }

    }

    private fun showAlertDialog(productId: String, price: Float) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext()).apply {
            setTitle("Remove product")
            setMessage("Do you want to remove product?")
            setPositiveButton("Yes") { dialogInterface, i ->
                viewModel.removeItemAndUpdate(productId, price)
                //Toast.makeText(requireContext(), "trash clicked", Toast.LENGTH_SHORT).show()
            }
            setNegativeButton("No") { dialogInterface, i ->
                dialogInterface.cancel()
            }
            setCancelable(true)
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()

    }

    private fun gotoAddressFragment() {
        val action = CartFragmentDirections.actionCartFragmentToAddressFragment()
        view?.findNavController()?.navigate(action)
    }

    private fun setTotalPriceView() {
        viewModel.cartModel.observe(viewLifecycleOwner) { cartModel ->
            binding.totalPrice.text = getString(R.string.currency) + String.format("%.2f", cartModel.total)
        }
    }

    private fun gotoProductDetailFragment(productId: String) {
        val action = CartFragmentDirections.actionCartFragmentToProductDetailFragment(productId)
        view?.findNavController()?.navigate(action)
    }
}