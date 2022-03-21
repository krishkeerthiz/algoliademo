package com.example.algoliademo1.ui.prductdetail

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.bumptech.glide.Glide
import com.example.algoliademo1.R
import com.example.algoliademo1.ShoppingApplication
import com.example.algoliademo1.databinding.FragmentProductDetailBinding
import com.example.algoliademo1.model.ProductModel
import com.example.algoliademo1.data.source.remote.FirebaseService
import com.example.algoliademo1.data.source.repository.ProductsRepository
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext

class ProductDetailFragment(
    val id: String) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentProductDetailBinding.bind(view)

        val viewModel = ViewModelProvider(requireActivity())[ProductDetailViewModel::class.java]

        viewModel.getCartModel()

        val productDocumentReference = FirebaseService.testGetProductReference(id)

        productDocumentReference.get().addOnSuccessListener {
            val productModel = it.toObject<ProductModel>()

            viewModel.productModel.value = productModel

            binding.productName.text = productModel?.name
            binding.productBrand.text = productModel?.brand

            binding.productPrice.text = "₹" + productModel?.price

            binding.productRating.rating = productModel?.rating?.div(2)?.toFloat() ?: 2.5f

            binding.productDescription.text = productModel?.description
            Glide.with(binding.productImage.context)
                .load(productModel?.image)
                .into(binding.productImage)
        }

     //   val application = ShoppingApplication()

//        val repository = ShoppingApplication.instance?.repository
//
//        val product = repository?.getProduct(id)?.asLiveData()!!

//        val productRepository = ProductsRepository.getRepository()
//
//        val product = productRepository.getProduct(id).asLiveData()
//
//        product.observe(viewLifecycleOwner){ products ->
//            if(products.isNotEmpty()){
//
//                val data = products[0]
//                //viewModel.productModel.value = productModel
//
//                binding.productName.text = data.name
//                binding.productBrand.text = data.brand
//
//                binding.productPrice.text = "₹" + data.price
//
//                binding.productRating.rating = data.rating?.div(2)?.toFloat() ?: 2.5f
//
//                binding.productDescription.text = data.description
//                Glide.with(binding.productImage.context)
//                    .load(data.image)
//                    .into(binding.productImage)
//            }
//        }


        // Sets card add visibility
        viewModel.cartModel.observe(viewLifecycleOwner){ cartModel ->  // sets button visibility
            if(cartModel != null){
                if(cartModel.products?.containsKey(id) == true){
                    binding.cartAdd.text = "Added to cart"
                    binding.cartAdd.isClickable = false
                }
                else{
                    binding.cartAdd.text = "Add to Cart"
                    binding.cartAdd.isClickable = true
                }
               // binding.cartAdd.visibility = View.VISIBLE
            }
        }

        binding.cartAdd.setOnClickListener {
            viewModel.addProductToCart(id)
            viewModel.getCartModel()
        }

        binding.wishlistButton.setOnClickListener{
            viewModel.addProductToWishlist(id)
        }

        binding.incrementButton.setOnClickListener {
            viewModel.incrementProductQuantity(id)
        }
    }
}