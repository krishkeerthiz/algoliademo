package com.example.algoliademo1.ui.wishlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.algoliademo1.data.source.local.entity.Product
import com.example.algoliademo1.data.source.remote.FirebaseService
import com.example.algoliademo1.data.source.repository.CartRepository
import com.example.algoliademo1.data.source.repository.ProductsRepository
import com.example.algoliademo1.data.source.repository.WishlistRepository
import com.example.algoliademo1.model.WishlistModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WishlistViewModel : ViewModel() {

    // Repositories
    private val wishlistRepository = WishlistRepository //.getRepository()
    private val cartRepository = CartRepository //.getRepository()
    private val productRepository = ProductsRepository //.getRepository()

    // Livedata
    private val _wishlistModel = MutableLiveData<WishlistModel>()
    val wishlistModel: LiveData<WishlistModel>
        get() = _wishlistModel

    fun getWishlistItems() {
        viewModelScope.launch {
            val productIds = wishlistRepository.getWishlist(FirebaseService.userId)
            val model = if (productIds.isEmpty()) WishlistModel(listOf()) else WishlistModel(productIds)
            _wishlistModel.value = model
        }
    }

    suspend fun getWishlistProducts(productIds: List<String>?): List<Product?> {
        var products = listOf<Product?>()
        viewModelScope.launch { 
            products = productRepository.getProducts(productIds)
        }.join()
       return products
    }

    fun removeFromWishlistAndUpdate(productId: String) {
        viewModelScope.launch {
            wishlistRepository.removeFromWishlist(FirebaseService.userId, productId)
            getWishlistItems()
        }
    }

    fun addToCart(productId: String, price: Float) {
        viewModelScope.launch {
            cartRepository.addToCart(productId, price)
        }
    }

//    suspend fun getProductCount(productId: String): Int {
//        val count = viewModelScope.async {
//            return@async cartRepository.getProductQuantity(FirebaseService.userId, productId)
//        }
//
//        return count.await()
//    }

    suspend fun getProductCount(productId: String) = withContext(Dispatchers.IO){
        cartRepository.getProductQuantity(FirebaseService.userId, productId)
    }


    private suspend fun removeFromWishlist(productId: String) {
        viewModelScope.launch {
            wishlistRepository.removeFromWishlist(FirebaseService.userId, productId)
        }.join()
    }

    suspend fun addAllToCart() {
        viewModelScope.launch {
            val productIds = wishlistRepository.getWishlist(FirebaseService.userId)

            for (productId in productIds) {
                val productCount = getProductCount(productId)

                if (productCount == 0)
                    cartRepository.addToCart(productId)

                removeFromWishlist(productId)
            }
        }.join()
        getWishlistItems()
    }
}