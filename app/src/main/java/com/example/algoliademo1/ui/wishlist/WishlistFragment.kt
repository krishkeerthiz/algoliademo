package com.example.algoliademo1.ui.wishlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.algoliademo1.ui.MainActivity
import com.example.algoliademo1.R
import com.example.algoliademo1.WishlistAdapter
import com.example.algoliademo1.WishlistClickListener
import com.example.algoliademo1.databinding.FragmentWishlistBinding

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
                {productId -> (requireActivity() as MainActivity).showProductDetailFragment(productId)},
                { productId, price -> viewModel.addToCart(productId, price)},
                {productId -> viewModel.removeFromWishlist(productId)}
            )
        )

        viewModel.wishlistModel.observe(viewLifecycleOwner){wishlistModel ->
            if(wishlistModel != null){
                wishlistAdapter.submitList(wishlistModel.products)
            }
        }

        viewModel.wishlistModel.observe(viewLifecycleOwner){wishlistModel ->
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
            viewModel.addAllToCart()
            Toast.makeText(requireContext(), "Products added to cart", Toast.LENGTH_SHORT).show()
        }
    }
}