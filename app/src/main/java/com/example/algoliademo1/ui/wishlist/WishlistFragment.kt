package com.example.algoliademo1.ui.wishlist

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.algoliademo1.ui.MainActivity
import com.example.algoliademo1.R
import com.example.algoliademo1.WishlistAdapter
import com.example.algoliademo1.WishlistClickListener
import com.example.algoliademo1.databinding.FragmentWishlistBinding
import com.example.algoliademo1.ui.cart.CartFragmentDirections
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WishlistFragment : Fragment() {

    private lateinit var binding: FragmentWishlistBinding
    private lateinit var viewModel: WishlistViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wishlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentWishlistBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[WishlistViewModel::class.java]

        viewModel.getWishlistItems()

        val wishlistAdapter = WishlistAdapter(
            WishlistClickListener(
                {productId -> gotoProductDetailFragment(productId)}, //(requireActivity() as MainActivity).showProductDetailFragment(productId)},
                { productId, price -> addToCart(productId, price)},
                {productId -> viewModel.removeFromWishlistAndUpdate(productId)}
            )
        )

        viewModel.wishlistModel.observe(viewLifecycleOwner){ wishlistModel ->
            if(wishlistModel != null){ // && !(wishlistModel.products.isNullOrEmpty())

                if(wishlistModel.products?.size == 0){
                    binding.emptyLayout.visibility = View.VISIBLE
                    binding.wishlistLayout.visibility = View.INVISIBLE
                }
                else{
                    binding.emptyLayout.visibility = View.INVISIBLE
                    binding.wishlistLayout.visibility = View.VISIBLE
                }

                binding.addAllToCartButton.isClickable = !wishlistModel.products.isNullOrEmpty()

                Log.d("wishlist adapter", wishlistModel.products.toString())
                wishlistAdapter.submitList(wishlistModel.products)
            }
        }

        viewModel.wishlistModel.observe(viewLifecycleOwner){ wishlistModel ->
            if(wishlistModel != null && !(wishlistModel.products.isNullOrEmpty())){
                binding.addAllToCartButton.isClickable = true
            }
        }

        binding.wishlistItemsList.let{
            it.adapter = wishlistAdapter
            it.itemAnimator = null
            it.layoutManager = LinearLayoutManager(requireContext())
        }

        binding.addAllToCartButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO){
                viewModel.addAllToCart()
            }
            //viewModel.getWishlistItems()
            //Toast.makeText(requireContext(), "Products added to cart", Toast.LENGTH_SHORT).show()
        }
    }

   private fun addToCart(productId: String, price: Float){
       CoroutineScope(Dispatchers.IO).launch {
           val productCount = viewModel.getProductCount(productId) //product count in cart

           if(productCount == 0)
               viewModel.addToCart(productId, price)

           viewModel.removeFromWishlistAndUpdate(productId)
       }
   }
    private fun gotoProductDetailFragment(productId: String) {
        val action = WishlistFragmentDirections.actionWishlistFragmentToProductDetailFragment(productId)
        view?.findNavController()?.navigate(action)
    }
}