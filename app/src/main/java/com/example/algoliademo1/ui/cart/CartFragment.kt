package com.example.algoliademo1.ui.cart

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.algoliademo1.R
import com.example.algoliademo1.databinding.FragmentCartBinding
import com.example.algoliademo1.model.ProductQuantityModel
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
        viewModel = ViewModelProvider(requireActivity())[CartViewModel::class.java]

        viewModel.getCartItems()
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCartBinding.bind(view)

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


        viewModel.cartModel.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.products?.size == 0) {
                    binding.emptyLayout.visibility = View.VISIBLE
                    binding.cartLayout.visibility = View.INVISIBLE
                } else {
                    binding.emptyLayout.visibility = View.INVISIBLE
                    binding.cartLayout.visibility = View.VISIBLE
                }

                val totalPrice = getString(R.string.currency) + String.format("%.2f", it.total)
                binding.totalPrice.text = totalPrice

                lifecycleScope.launch(Dispatchers.IO){
                    val productsQuantity = mutableListOf<ProductQuantityModel>()
                    val products = viewModel.getCartProducts(it.products?.keys?.toList())

                    for(product in products)
                        productsQuantity.add(ProductQuantityModel(product, it.products?.get(product.productId)!!))

                    withContext(Dispatchers.Main){
                        cartAdapter.submitList(productsQuantity)
                    }
                }
                //cartAdapter.submitList(it.products?.keys?.toList())
               // cartAdapter.notifyDataSetChanged()

            }
        }

        binding.cartItemsList.apply {
            itemAnimator = null
            adapter = cartAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

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