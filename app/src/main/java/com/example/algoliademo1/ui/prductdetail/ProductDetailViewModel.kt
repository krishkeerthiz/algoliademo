package com.example.algoliademo1.ui.prductdetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.algoliademo1.data.source.remote.FirebaseService
import com.example.algoliademo1.data.source.repository.CartRepository
import com.example.algoliademo1.data.source.repository.ProductsRepository
import com.example.algoliademo1.data.source.repository.WishlistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductDetailViewModel : ViewModel() {

    // Repositories
    private val wishlistRepository = WishlistRepository.getRepository()
    private val cartRepository = CartRepository.getRepository()
    private val productRepository = ProductsRepository.getRepository()

    val isInCart = MutableLiveData<Boolean>()

    fun addProductToWishlist(productId: String) {
        viewModelScope.launch {
            wishlistRepository.addToWishlist(FirebaseService.userId, productId)
        }
    }

    fun addProductToCart(productId: String) {
        viewModelScope.launch {
            cartRepository.addToCart(productId)
        }
    }

    fun removeProductFromWishlist(id: String) {
        viewModelScope.launch {
            wishlistRepository.removeFromWishlist(FirebaseService.userId, id)
        }
    }

    suspend fun isProductInCart(id: String): Boolean {
        val result = viewModelScope.async {
            cartRepository.isProductInCart(FirebaseService.userId, id)
        }
        return result.await()
    }

    suspend fun isInWishlist(productId: String): Boolean {
        val result = viewModelScope.async {
            wishlistRepository.isInWishlist(FirebaseService.userId, productId)
        }

        return result.await()
    }

    suspend fun getProduct(productId: String) = withContext(Dispatchers.IO) {
        productRepository.getProduct(productId)
    }
}