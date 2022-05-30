package com.example.algoliademo1.ui.wishlist

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.algoliademo1.R
import com.example.algoliademo1.databinding.FragmentWishlistBinding
import kotlinx.coroutines.launch

class WishlistFragment : Fragment() {

    private lateinit var binding: FragmentWishlistBinding
    private val viewModel: WishlistViewModel by viewModels {
        WishlistViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_wishlist, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentWishlistBinding.bind(view)

        viewModel.getWishlistItems()

        // Adapter
        val wishlistAdapter = WishlistAdapter(
            WishlistClickListener(
                { productId -> gotoProductDetailFragment(productId) }, //(requireActivity() as MainActivity).showProductDetailFragment(productId)},
                { productId, price -> addToCart(productId, price) },
                { productId -> showAlertDialog(productId) }
            )
        )

        // Recyclerview
        binding.wishlistItemsList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = wishlistAdapter
        }

        // Livedata
        viewModel.wishlistModel.observe(viewLifecycleOwner) { wishlistModel ->
            if (wishlistModel != null) { // && !(wishlistModel.products.isNullOrEmpty())

                if (wishlistModel.products?.size == 0) {
                    binding.emptyLayout.visibility = View.VISIBLE
                    binding.wishlistLayout.visibility = View.INVISIBLE
                } else {
                    binding.emptyLayout.visibility = View.INVISIBLE
                    binding.wishlistLayout.visibility = View.VISIBLE

                    // binding.addAllToCartButton.isClickable = !wishlistModel.products.isNullOrEmpty()

                    lifecycleScope.launch {
                        val wishlistProducts = viewModel.getWishlistProducts(wishlistModel.products)
                        wishlistAdapter.addWishlistProducts(wishlistProducts)
                    }
                }

            }
        }

        // Add all to cart button
        binding.addAllToCartButton.setOnClickListener {
            lifecycleScope.launch {
                viewModel.addAllToCart()
            }
            Toast.makeText(requireContext(), "Products added to cart", Toast.LENGTH_SHORT).show()
        }
    }

    //    private fun addToCart(productId: String?, price: Float) {
//        if(productId != null){
//            lifecycleScope.launch(Dispatchers.IO) {
//                val cartProductCount = viewModel.getProductCount(productId)
//
//                if (cartProductCount == null || cartProductCount == 0)
//                    viewModel.addToCart(productId, price)
//
//                viewModel.removeFromWishlistAndUpdate(productId)
//            }
//        }
//    }
    private fun addToCart(productId: String?, price: Float) {
        if (productId != null) {
            lifecycleScope.launch {
                val cartProductCount = viewModel.getProductCount(productId)

                if (cartProductCount == null || cartProductCount == 0)
                    viewModel.addToCart(productId, price)

                viewModel.removeFromWishlistAndUpdate(productId)
            }
        }
    }

    private fun gotoProductDetailFragment(productId: String?) {
        if (productId != null) {
            val action =
                WishlistFragmentDirections.actionWishlistFragmentToProductDetailFragment(productId)
            view?.findNavController()?.navigate(action)
        }

    }

    private fun showAlertDialog(productId: String?) {
        if (productId != null) {
            val alertDialogBuilder = AlertDialog.Builder(requireContext()).apply {

                setTitle("Remove product")
                setMessage("Do you want to remove product?")

                setPositiveButton("Yes") { _, _ ->
                    viewModel.removeFromWishlistAndUpdate(productId)
                    Toast.makeText(requireContext(), "removed from wishlist", Toast.LENGTH_SHORT)
                        .show()
                }

                setNegativeButton("No") { dialogInterface, _ ->
                    dialogInterface.cancel()
                }
                setCancelable(true)
            }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
    }
}