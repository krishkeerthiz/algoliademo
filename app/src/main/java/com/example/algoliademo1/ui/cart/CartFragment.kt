package com.example.algoliademo1.ui.cart

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.algoliademo1.R
import com.example.algoliademo1.databinding.FragmentCartBinding
import kotlinx.coroutines.launch

class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding

    private val viewModel: CartViewModel by viewModels {
        CartViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel.getCartItems()  // Need to check
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Binding
        binding = FragmentCartBinding.bind(view)

        // Adapter
        val cartAdapter = CartAdapter(CartOnClickListener(
            { productId -> gotoProductDetailFragment(productId) },
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

        // Recyclerview
        binding.cartItemsList.apply {
            adapter = cartAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        // Live data
        viewModel.cartModelLiveData.observe(viewLifecycleOwner) { cartModel ->
            if (cartModel != null) {
                if (cartModel.products?.size == 0) {
                    binding.emptyLayout.visibility = View.VISIBLE
                    binding.cartLayout.visibility = View.INVISIBLE
                } else {
                    binding.emptyLayout.visibility = View.INVISIBLE
                    binding.cartLayout.visibility = View.VISIBLE

                    val totalPrice =
                        getString(R.string.currency) + String.format("%.2f", cartModel.total)
                    binding.totalPrice.text = totalPrice

                    lifecycleScope.launch {

                        val productsQuantity = viewModel.getProductsQuantity(cartModel)

                        cartAdapter.addProductQuantities(productsQuantity)

                    }
                }

            }
        }

        // Buy button
        binding.buyButton.setOnClickListener {
            gotoAddressFragment()
        }

    }

    private fun showAlertDialog(productId: String, price: Float) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext()).apply {
            setTitle("Remove product")
            setMessage("Do you want to remove product?")
            setPositiveButton("Yes") { _, _ ->
                viewModel.removeItemAndUpdate(productId, price)
            }
            setNegativeButton("No") { dialogInterface, _ ->
                dialogInterface.cancel()
            }
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun gotoAddressFragment() {
        val action = CartFragmentDirections.actionCartFragmentToAddressFragment()
        view?.findNavController()?.navigate(action)
    }


    private fun gotoProductDetailFragment(productId: String) {
        val action = CartFragmentDirections.actionCartFragmentToProductDetailFragment(productId)
        view?.findNavController()?.navigate(action)
    }
}