package com.example.algoliademo1.ui.wishlist

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.algoliademo1.R
import com.example.algoliademo1.databinding.FragmentWishlistBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WishlistFragment : Fragment() {

    private lateinit var binding: FragmentWishlistBinding
    private lateinit var viewModel: WishlistViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_wishlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentWishlistBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[WishlistViewModel::class.java]

        viewModel.getWishlistItems()

        val wishlistAdapter = WishlistAdapter(
            WishlistClickListener(
                { productId -> gotoProductDetailFragment(productId) }, //(requireActivity() as MainActivity).showProductDetailFragment(productId)},
                { productId, price -> addToCart(productId, price) },
                { productId -> showAlertDialog(productId) }
            )
        )

        viewModel.wishlistModel.observe(viewLifecycleOwner) { wishlistModel ->
            if (wishlistModel != null) { // && !(wishlistModel.products.isNullOrEmpty())

                if (wishlistModel.products?.size == 0) {
                    binding.emptyLayout.visibility = View.VISIBLE
                    binding.wishlistLayout.visibility = View.INVISIBLE
                } else {
                    binding.emptyLayout.visibility = View.INVISIBLE
                    binding.wishlistLayout.visibility = View.VISIBLE
                }

                binding.addAllToCartButton.isClickable = !wishlistModel.products.isNullOrEmpty()

                lifecycleScope.launch(Dispatchers.IO) {
                    val wishlistProducts = viewModel.getWishlistProducts(wishlistModel.products)
                    withContext(Dispatchers.Main){
                        wishlistAdapter.submitList(wishlistProducts)
                    }

                }

            }
        }

//        viewModel.wishlistModel.observe(viewLifecycleOwner) { wishlistModel ->
//            if (wishlistModel != null && !(wishlistModel.products.isNullOrEmpty())) {
//                binding.addAllToCartButton.isClickable = true
//            }
//        }

        binding.wishlistItemsList.let {
            it.adapter = wishlistAdapter
            it.itemAnimator = null // Need to look
            it.layoutManager = LinearLayoutManager(requireContext())
        }

        binding.addAllToCartButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.addAllToCart()
            }
          Toast.makeText(requireContext(), "Products added to cart", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addToCart(productId: String, price: Float) {
        CoroutineScope(Dispatchers.IO).launch {
            val productCount = viewModel.getProductCount(productId) //product count in cart

            Log.d(TAG, "addToCart: $productCount")
            if (productCount == 0)
                viewModel.addToCart(productId, price)

            viewModel.removeFromWishlistAndUpdate(productId)
        }
    }

    private fun gotoProductDetailFragment(productId: String) {
        val action =
            WishlistFragmentDirections.actionWishlistFragmentToProductDetailFragment(productId)
        view?.findNavController()?.navigate(action)
    }

    private fun showAlertDialog(productId: String) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext()).apply {
            setTitle("Remove product")
            setMessage("Do you want to remove product?")
            setPositiveButton("Yes") { _, _ ->
                viewModel.removeFromWishlistAndUpdate(productId)
                Toast.makeText(requireContext(), "removed from wishlist", Toast.LENGTH_SHORT).show()
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