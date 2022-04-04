package com.example.algoliademo1.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.algoliademo1.data.source.local.entity.ItemCount
import com.example.algoliademo1.model.CartModel
import com.example.algoliademo1.data.source.remote.FirebaseService
import com.example.algoliademo1.data.source.repository.CartRepository
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.launch

class CartViewModel : ViewModel(){

    private val cartRepository = CartRepository.getRepository()

    private val _cartModel = MutableLiveData<CartModel>()

    val cartModel : LiveData<CartModel>
    get() = _cartModel

    // New code
    fun getCartItems() {
        viewModelScope.launch {
            val items = cartRepository.getCartItems(FirebaseService.userId)

            val productsQuantity: Map<String, Int> = listToMap(items)
            val total = cartRepository.getCartTotal(FirebaseService.userId)

            val model = CartModel(productsQuantity, total)

            _cartModel.value = model
        }

    }

    private fun listToMap(items: List<ItemCount>): Map<String, Int>{
        val productsQuantity = mutableMapOf<String, Int>()

        for(item in items)
            productsQuantity[item.productId] = item.quantity

        return productsQuantity
    }

    fun removeItemAndUpdate(productId: String, price: Float){
        viewModelScope.launch {
            cartRepository.removeFromCart(FirebaseService.userId, productId)
            getCartItems()
        }
    }

    // Old code

//    fun getCartItems() {
//        val cartRef = FirebaseService.getCartReference()
//
//        cartRef.get().addOnSuccessListener {
//            val model = it.toObject<CartModel>()
//
//            if(model != null)
//            _cartModel.value = model
//            else
//                _cartModel.value = null
//        }
//    }

//    fun removeItemAndUpdate(productId: String, price: Float){
//        FirebaseService.removeFromCart(productId, price)
//        getCartItems()
//    }
}