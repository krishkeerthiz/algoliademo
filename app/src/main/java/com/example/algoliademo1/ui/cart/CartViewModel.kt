package com.example.algoliademo1.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.algoliademo1.model.CartModel
import com.example.algoliademo1.data.source.remote.FirebaseService
import com.google.firebase.firestore.ktx.toObject

class CartViewModel : ViewModel(){
    private val _cartModel = MutableLiveData<CartModel>()

    val cartModel : LiveData<CartModel>
    get() = _cartModel

    fun getCartItems() {
        val cartRef = FirebaseService.getCartReference()

        cartRef.get().addOnSuccessListener {
            val model = it.toObject<CartModel>()

            if(model != null)
            _cartModel.value = model
            else
                _cartModel.value = null
        }
    }

    fun removeItemAndUpdate(productId: String, price: Float){
        FirebaseService.removeFromCart(productId, price)
        getCartItems()
    }
}