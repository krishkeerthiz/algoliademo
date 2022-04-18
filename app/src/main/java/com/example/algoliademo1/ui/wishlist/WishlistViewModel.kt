package com.example.algoliademo1.ui.wishlist

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.algoliademo1.data.source.remote.FirebaseService
import com.example.algoliademo1.data.source.repository.CartRepository
import com.example.algoliademo1.data.source.repository.WishlistRepository
import com.example.algoliademo1.model.WishlistModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class WishlistViewModel : ViewModel() {

    private val wishlistRepository = WishlistRepository.getRepository()
    private val cartRepository = CartRepository.getRepository()

    private val _wishlistModel = MutableLiveData<WishlistModel>()

    val wishlistModel: LiveData<WishlistModel>
        get() = _wishlistModel

    fun getWishlistItems() {
        viewModelScope.launch {
            val productIds = wishlistRepository.getWishlist(FirebaseService.userId)
            val model =
                if (productIds.isEmpty()) WishlistModel(listOf()) else WishlistModel(productIds)
            Log.d(TAG, "getWishlistItems: $productIds")
            _wishlistModel.value = model

        }
    }

    fun removeFromWishlistAndUpdate(productId: String) {
        viewModelScope.launch {
            wishlistRepository.removeFromWishlist(FirebaseService.userId, productId)
            Log.d("check product id", productId)
            getWishlistItems()
        }
    }

    fun addToCart(productId: String, price: Float) {
        viewModelScope.launch {
            cartRepository.addToCart(productId, price)
        }

    }

    suspend fun getProductCount(productId: String): Int {
        val count = viewModelScope.async {
            return@async cartRepository.getProductQuantity(FirebaseService.userId, productId)
        }

        return count.await()
    }

    suspend fun removeFromWishlist(productId: String) {
        viewModelScope.launch {
            wishlistRepository.removeFromWishlist(FirebaseService.userId, productId)
        }.join()
        Log.d(TAG, "addAllToCart: remove after join")
    }

    suspend fun addAllToCart() {
        viewModelScope.launch {
            val productIds = wishlistRepository.getWishlist(FirebaseService.userId)

            Log.d(TAG, "addAllToCart: ${productIds.size}")
            for (productId in productIds) {
                val productCount = getProductCount(productId) //product count in cart

                if (productCount == 0)
                    cartRepository.addToCart(productId)

                removeFromWishlist(productId)
                Log.d(TAG, "addAllToCart: $productId")
                 cartRepository.addToCart(productId) // Add price if using firebase

            }

        }.join()
        Log.d(TAG, "addAllToCart: after join")
        getWishlistItems()
    }

}