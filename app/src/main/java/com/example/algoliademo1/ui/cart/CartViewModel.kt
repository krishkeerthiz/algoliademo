package com.example.algoliademo1.ui.cart

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.algoliademo1.data.source.local.entity.ItemCount
import com.example.algoliademo1.data.source.remote.FirebaseService
import com.example.algoliademo1.data.source.repository.CartRepository
import com.example.algoliademo1.model.CartModel
import kotlinx.coroutines.launch

class CartViewModel : ViewModel() {

    private val cartRepository = CartRepository.getRepository()

    private val _cartModel = MutableLiveData<CartModel>()

    val cartModel: LiveData<CartModel>
        get() = _cartModel

    fun getCartItems() {
        viewModelScope.launch {
            val items = cartRepository.getCartItems(FirebaseService.userId)

            Log.d(TAG, "getCartItems: ${items.toString()}")
            Log.d(TAG, "getCartItems: ${FirebaseService.userId}")

            val productsQuantity: Map<String, Int> = listToMap(items)
            val total = cartRepository.getCartTotal(FirebaseService.userId)

            val model = CartModel(productsQuantity, total)

            _cartModel.value = model
        }
    }

    private fun listToMap(items: List<ItemCount>): Map<String, Int> {
        val productsQuantity = mutableMapOf<String, Int>()

        for (item in items) {
            productsQuantity[item.productId] = item.quantity
            Log.d(TAG, "listToMap: $item.quantity")
        }
        return productsQuantity
    }

    fun removeItemAndUpdate(productId: String, price: Float) {
        viewModelScope.launch {
            cartRepository.removeFromCart(FirebaseService.userId, productId)
            getCartItems()
        }
    }

    fun incrementItemAndUpdate(productId: String) {
        viewModelScope.launch {
            cartRepository.incrementItemCount(FirebaseService.userId, productId)
            getCartItems()
        }
    }

    fun decrementItemAndUpdate(productId: String) {
        viewModelScope.launch {
            cartRepository.decrementItemCount(FirebaseService.userId, productId)
            getCartItems()
        }
    }

}