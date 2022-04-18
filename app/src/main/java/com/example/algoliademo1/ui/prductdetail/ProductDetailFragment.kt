package com.example.algoliademo1.ui.prductdetail

import android.app.Application
import android.content.ContentValues.TAG
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavArgs
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.algoliademo1.R
import com.example.algoliademo1.ShoppingApplication
import com.example.algoliademo1.data.source.local.entity.Product
import com.example.algoliademo1.databinding.FragmentProductDetailBinding
import com.example.algoliademo1.model.ProductModel
import com.example.algoliademo1.data.source.remote.FirebaseService
import com.example.algoliademo1.data.source.repository.ProductsRepository
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductDetailFragment : Fragment() {
    private val args: ProductDetailFragmentArgs by navArgs()
    private lateinit var binding: FragmentProductDetailBinding
    private lateinit var viewModel: ProductDetailViewModel
    private val productRepository = ProductsRepository.getRepository()

    private val product = MutableLiveData<Product>()

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

        viewModel.getCartModel()

        refreshCartButton(id)

//        val productDocumentReference = FirebaseService.testGetProductReference(id)
//
//        productDocumentReference.get().addOnSuccessListener {
//            val productModel = it.toObject<ProductModel>()
//
//            viewModel.productModel.value = productModel
//
//            binding.productName.text = productModel?.name
//            binding.productBrand.text = productModel?.brand
//
//            binding.productPrice.text = "₹" + productModel?.price
//
//            binding.productRating.rating = productModel?.rating?.div(2)?.toFloat() ?: 2.5f
//
//            binding.productDescription.text = productModel?.description
//            Glide.with(binding.productImage.context)
//                .load(productModel?.image)
//                .into(binding.productImage)
//        }

        //   val application = ShoppingApplication()

//        val repository = ShoppingApplication.instance?.repository
//
//        val product = repository?.getProduct(id)?.asLiveData()!!

//        lifecycleScope.launch(Dispatchers.IO) {
//            product.value = productRepository.getProduct(id)
//
//            product.observe(viewLifecycleOwner) { product ->
//                if (product != null) {
//
//                    withContext(Dispatchers.Main){
//                        binding.productName.text = product.name
//                        binding.productBrand.text = product.brand
//
//                        binding.productPrice.text = "₹" + product.price
//
//                        binding.productRating.rating = product.rating?.div(2)?.toFloat() ?: 2.5f
//
//                        binding.productDescription.text = product.description
//                        Glide.with(binding.productImage.context)
//                            .load(product.image)
//                            .into(binding.productImage)
//                    }
//
//                }
//            }
//        }

        lifecycleScope.launch(Dispatchers.Main) {
            Log.d(TAG, "onViewCreated: $id")
            val product = withContext(Dispatchers.IO) {
                productRepository.getProduct(id)
            }
            Log.d(TAG, "onViewCreated: ${product.toString()}")
           // viewModel.productModel.value = productModel
            withContext(Dispatchers.Main){

                binding.productName.text = product.name
                binding.productBrand.text = product.brand

                binding.productPrice.text = getString(R.string.currency) + product.price

                binding.productRating.rating = product.rating?.div(2)?.toFloat() ?: 2.5f

                binding.productDescription.text = product.description
                Glide.with(binding.productImage.context)
                    .load(product.image)
                    .placeholder(R.drawable.spinner1)
                    .into(binding.productImage)
            }
        }


//
//        product.observe(viewLifecycleOwner) { products ->
//
//        }

        // Sets card add visibility
        viewModel.cartModel.observe(viewLifecycleOwner) { cartModel ->  // sets button visibility
            if (cartModel != null) {
                if (cartModel.products?.containsKey(id) == true) {

                    //binding.cartAdd.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.green_greyed))
                    //binding.cartAdd.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green_greyed))
                    binding.cartAdd.text = "Added to cart"
                    binding.cartAdd.isClickable = false
//                    binding.gotoCart.visibility = View.VISIBLE
//                    binding.cartAdd.visibility = View.INVISIBLE
                } else {

                    //binding.cartAdd.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.green))
                    //binding.cartAdd.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
                    binding.cartAdd.text = "Add to Cart"
                    binding.cartAdd.isClickable = true
//                    binding.gotoCart.visibility = View.INVISIBLE
//                    binding.cartAdd.visibility = View.VISIBLE
                }
                // binding.cartAdd.visibility = View.VISIBLE
            }
        }


        binding.cartAdd.setOnClickListener {
            Toast.makeText(requireContext(), "Product added to cart", Toast.LENGTH_SHORT).show()
            viewModel.addProductToCart(id)
            viewModel.getCartModel()
            refreshCartButton(id, true)
        }

        binding.gotoCart.setOnClickListener{
            val action = ProductDetailFragmentDirections.actionProductDetailFragmentToCartFragment()
            view?.findNavController()?.navigate(action)
        }

        //set icon state while entering the page
        lifecycleScope.launch(Dispatchers.IO) {
            val result = viewModel.isInWishlist(id)
            withContext(Dispatchers.Main) {
                if (result) {
                    binding.wishlistButton.setImageDrawable(context?.getDrawable(R.drawable.ic_heart_red))
                } else {
                    binding.wishlistButton.setImageDrawable(context?.getDrawable(R.drawable.ic_heart))
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
                        binding.wishlistButton.setImageDrawable(context?.getDrawable(R.drawable.ic_heart))
                    } else {
                        viewModel.addProductToWishlist(id)
                        binding.wishlistButton.setImageDrawable(context?.getDrawable(R.drawable.ic_heart_red))
                        binding.wishlistAnimation.likeAnimation()
                    }
                    Log.d(TAG, "onViewCreated: $result")
                }
            }
        }

    }

    private fun refreshCartButton(id: String, default: Boolean = false) {
        lifecycleScope.launch(Dispatchers.IO) {
            val result = viewModel.isProductInCart(id)
            withContext(Dispatchers.Main) {
                if (result || default) {
//                    binding.gotoCart.visibility = View.VISIBLE
//                    binding.cartAdd.visibility = View.INVISIBLE
                    binding.cartAdd.text = "Added to cart"
                    binding.cartAdd.isClickable = false
                } else {
//                    binding.gotoCart.visibility = View.INVISIBLE
//                    binding.cartAdd.visibility = View.VISIBLE
                    binding.cartAdd.text = "Add to cart"
                    binding.cartAdd.isClickable = true
                }
            }
        }
    }
}