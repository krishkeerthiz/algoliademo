package com.example.algoliademo1.ui.productdetail

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.algoliademo1.data.source.remote.FirebaseService
import com.example.algoliademo1.data.source.repository.CartRepository
import com.example.algoliademo1.data.source.repository.ProductsRepository
import com.example.algoliademo1.data.source.repository.WishlistRepository
import kotlinx.coroutines.launch

class ProductDetailViewModel(context: Context) : ViewModel() {

    // Repositories
    private val wishlistRepository = WishlistRepository.getRepository(context)
    private val cartRepository = CartRepository.getRepository(context)
    private val productRepository = ProductsRepository.getRepository(context)

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

    suspend fun isProductInCart(id: String): Boolean =
        cartRepository.isProductInCart(FirebaseService.userId, id)

    suspend fun isInWishlist(productId: String): Boolean =
        wishlistRepository.isInWishlist(FirebaseService.userId, productId)

    suspend fun getProduct(productId: String) =
        productRepository.getProduct(productId)

}

class ProductDetailViewModelFactory(private val context: Context) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProductDetailViewModel(context) as T
    }
}