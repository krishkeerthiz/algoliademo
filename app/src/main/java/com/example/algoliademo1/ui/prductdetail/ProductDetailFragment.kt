package com.example.algoliademo1.ui.prductdetail

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.algoliademo1.R
import com.example.algoliademo1.data.source.repository.ProductsRepository
import com.example.algoliademo1.databinding.FragmentProductDetailBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductDetailFragment : Fragment() {
    private val args: ProductDetailFragmentArgs by navArgs()
    private lateinit var binding: FragmentProductDetailBinding
    private lateinit var viewModel: ProductDetailViewModel
    private val productRepository = ProductsRepository.getRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val id: String = args.id

        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProductDetailBinding.bind(view)

        viewModel = ViewModelProvider(requireActivity())[ProductDetailViewModel::class.java]

        lifecycleScope.launch {
            viewModel.isInCart.value = viewModel.isProductInCart(id)
        }

        viewModel.isInCart.observe(viewLifecycleOwner){
            if(it){
                binding.cartAdd.text = getString(R.string.added_to_cart)
            }
            else{
                binding.cartAdd.text = getString(R.string.add_to_cart)
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            Log.d(TAG, "onViewCreated: $id")
            val product = withContext(Dispatchers.IO) {
                productRepository.getProduct(id)
            }

            withContext(Dispatchers.Main){

                binding.productName.text = product.name
                binding.productBrand.text = product.brand

                val price = getString(R.string.currency) + product.price
                binding.productPrice.text = price

                binding.productRating.rating = product.rating.div(2).toFloat()

                binding.productDescription.text = product.description
                Glide.with(binding.productImage.context)
                    .load(product.image)
                    .placeholder(R.drawable.spinner1)
                    .into(binding.productImage)
            }
        }


        binding.cartAdd.setOnClickListener {
            if(viewModel.isInCart.value == false){
                viewModel.isInCart.value = true
                viewModel.addProductToCart(id)
               // refreshCartButton(id, true)
                Toast.makeText(requireContext(), "Added to cart", Toast.LENGTH_SHORT).show()
            }
            Toast.makeText(requireContext(), "Already in cart", Toast.LENGTH_SHORT).show()


        }

        // view is set to invisible, so no use
//        binding.gotoCart.setOnClickListener{
//            val action = ProductDetailFragmentDirections.actionProductDetailFragmentToCartFragment()
//            view?.findNavController()?.navigate(action)
//        }

        //set icon state while entering the page
        lifecycleScope.launch(Dispatchers.IO) {
            val result = viewModel.isInWishlist(id)
            withContext(Dispatchers.Main) {
                if (result) {
                    binding.wishlistButton.setImageDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.ic_heart_red))
                } else {
                    binding.wishlistButton.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_heart))
                }
            }
        }

        // set icon state while click
        binding.wishlistButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val result = viewModel.isInWishlist(id)
                Log.d(TAG, "onViewCreated: result before dispatcher main")
                withContext(Dispatchers.Main) {
                    Log.d(TAG, "onViewCreated: result during dispatcher main")
                    if (result) {
                        Log.d(TAG, "onViewCreated: ")
                        viewModel.removeProductFromWishlist(id)
                        binding.wishlistButton.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_heart))
                    } else {
                        viewModel.addProductToWishlist(id)
                        binding.wishlistButton.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_heart_red))
                        binding.wishlistAnimation.likeAnimation()
                    }
                    Log.d(TAG, "onViewCreated: $result")
                }
            }
        }

    }

}