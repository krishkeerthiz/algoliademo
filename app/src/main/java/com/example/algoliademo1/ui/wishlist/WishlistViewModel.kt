package com.example.algoliademo1.ui.wishlist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.algoliademo1.model.ProductModel
import com.example.algoliademo1.model.WishlistModel
import com.example.algoliademo1.data.source.remote.FirebaseService
import com.google.firebase.firestore.ktx.toObject

class WishlistViewModel : ViewModel() {
    private val _wishlistModel = MutableLiveData<WishlistModel>()

    val wishlistModel : LiveData<WishlistModel>
        get() = _wishlistModel

    fun getWishlistItems() {
        val wishlistRef = FirebaseService.getWishlistReference()

        wishlistRef.get().addOnSuccessListener {
            val model = it.toObject<WishlistModel>()

            _wishlistModel.value = model!!
        }
    }

    fun addToCart(productId: String, price: Float){
        FirebaseService.addToCart(productId, price)
        removeFromWishlistAndUpdate(productId)
    }

    fun removeFromWishlistAndUpdate(productId: String){
        FirebaseService.removeFromWishlist(productId)
        getWishlistItems()
    }

    fun removeFromWishlist(productId: String){
        FirebaseService.removeFromWishlist(productId)
    }

    fun addAllToCart(){
        val productsList = wishlistModel.value!!.products!!

        productsList.forEach{ productId ->
            val productDocumentReference = FirebaseService.testGetProductReference(productId)

            productDocumentReference.get().addOnSuccessListener {
                val productModel = it.toObject<ProductModel>()
                FirebaseService.addToCart(productId, productModel!!.price)
                Log.d("Wishlist", "product added to cart from add all to cart button")
            }
                .addOnFailureListener {
                    Log.d("Wishlist", "Failed to add product from add all to cart button")
                }
        }

        productsList.forEach { productId ->
            removeFromWishlist(productId)
        }

        getWishlistItems()
    }
}