package com.example.algoliademo1.ui.prductdetail

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.algoliademo1.ShoppingApplication
import com.example.algoliademo1.model.CartModel
import com.example.algoliademo1.model.ProductModel
import com.example.algoliademo1.data.source.remote.FirebaseService
import com.example.algoliademo1.data.source.repository.CartRepository
import com.example.algoliademo1.data.source.repository.WishlistRepository
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.launch

class ProductDetailViewModel: ViewModel() {
    // New code
    private val wishlistRepository = WishlistRepository.getRepository()
    private val cartRepository = CartRepository.getRepository()

    fun addProductToWishlist(productId: String){
        viewModelScope.launch{
            wishlistRepository.addToWishlist(FirebaseService.userId, productId)
        }
    }

    fun addProductToCart(productId: String){
        viewModelScope.launch {
            cartRepository.addToCart(productId)
        }
    }

    fun incrementProductQuantity(productId: String){
        viewModelScope.launch {
            cartRepository.incrementItemCount(FirebaseService.userId, productId)
        }
    }

    // Old  code
    val productModel = MutableLiveData<ProductModel>()

    val cartModel = MutableLiveData<CartModel>()

//    fun addProductToCart(productId: String){
//        val model = productModel.value!!
//
//        FirebaseService.addToCart(productId ,model.price)
//    }

//    fun addProductToWishlist(productId: String){
//        val model = productModel.value!!
//
//        FirebaseService.addToWishlist(productId)
//    }

//    fun incrementProductQuantity(productId: String){
//        val model = productModel.value!!
//
//        FirebaseService.incrementProductQuantity(productId ,model.price)
//    }

    fun getCartModel() {
        val cartDocRef = FirebaseService.getCartReference()
        cartDocRef.get().addOnSuccessListener {
            cartModel.value = it.toObject<CartModel>()
            Log.d("Product detail fragment", "Cart model loaded")
        }
            .addOnFailureListener{
                Log.d("Product detail fragment", "Cart model failed to load")
            }
    }
}