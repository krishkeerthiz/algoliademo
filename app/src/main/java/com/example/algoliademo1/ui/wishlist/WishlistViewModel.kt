package com.example.algoliademo1.ui.wishlist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.algoliademo1.ShoppingApplication
import com.example.algoliademo1.model.ProductModel
import com.example.algoliademo1.model.WishlistModel
import com.example.algoliademo1.data.source.remote.FirebaseService
import com.example.algoliademo1.data.source.repository.CartRepository
import com.example.algoliademo1.data.source.repository.WishlistRepository
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.launch
import okio.utf8Size

class WishlistViewModel : ViewModel() {

    //New code
    private val wishlistRepository = WishlistRepository.getRepository()
    private val cartRepository = CartRepository.getRepository()


    fun getWishlistItems(){
        viewModelScope.launch {
            val productIds = wishlistRepository.getWishlist(FirebaseService.userId)
           // val x = productIds.toString()
            val model = if(productIds.isEmpty()) WishlistModel(listOf()) else WishlistModel(productIds)
            _wishlistModel.value = model

        }
    }


    fun removeFromWishlistAndUpdate(productId: String){
        viewModelScope.launch {
            wishlistRepository.removeFromWishlist(FirebaseService.userId, productId)
            Log.d("check product id", productId)
            getWishlistItems()
        }
    }

    fun addToCart(productId: String, price: Float){
        viewModelScope.launch {
            cartRepository.addToCart(productId, price)
            removeFromWishlistAndUpdate(productId)
        }

    }

    fun removeFromWishlist(productId: String){
        viewModelScope.launch {
            wishlistRepository.removeFromWishlist(FirebaseService.userId, productId)
        }
    }

    fun addAllToCart(){
        viewModelScope.launch {
            val productIds = wishlistRepository.getWishlist(FirebaseService.userId)

            for(productId in productIds){
                cartRepository.addToCart(productId) // Add price if using firebase
                removeFromWishlist(productId)
            }

            getWishlistItems()
        }


    }

    //Old code
    private val _wishlistModel = MutableLiveData<WishlistModel>()

    val wishlistModel : LiveData<WishlistModel>
        get() = _wishlistModel

    // Firebase
//    fun getWishlistItems() {
//        val wishlistRef = FirebaseService.getWishlistReference()
//
//        wishlistRef.get().addOnSuccessListener {
//            val model = it.toObject<WishlistModel>()
//
//            _wishlistModel.value = model!!
//        }
//    }

//    fun addToCart(productId: String, price: Float){
//        FirebaseService.addToCart(productId, price)
//        removeFromWishlistAndUpdate(productId)
//    }

//    fun removeFromWishlistAndUpdate(productId: String){
//        FirebaseService.removeFromWishlist(productId)
//        getWishlistItems()
//    }

//    fun removeFromWishlist(productId: String){
//        FirebaseService.removeFromWishlist(productId)
//    }

//    fun addToCart(productId: String, price: Float){
//        FirebaseService.addToCart(productId, price)
//        removeFromWishlistAndUpdate(productId)
//    }

//    fun addAllToCart(){
//        val productsList = wishlistModel.value!!.products!!
//
//        productsList.forEach{ productId ->
//            val productDocumentReference = FirebaseService.testGetProductReference(productId)
//
//            productDocumentReference.get().addOnSuccessListener {
//                val productModel = it.toObject<ProductModel>()
//                FirebaseService.addToCart(productId, productModel!!.price)
//                Log.d("Wishlist", "product added to cart from add all to cart button")
//            }
//                .addOnFailureListener {
//                    Log.d("Wishlist", "Failed to add product from add all to cart button")
//                }
//        }
//
//        productsList.forEach { productId ->
//            removeFromWishlist(productId)
//        }
//
//        getWishlistItems()
//    }
}