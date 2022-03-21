package com.example.algoliademo1.ui.prductdetail

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.algoliademo1.model.CartModel
import com.example.algoliademo1.model.ProductModel
import com.example.algoliademo1.data.source.remote.FirebaseService
import com.google.firebase.firestore.ktx.toObject

class ProductDetailViewModel: ViewModel() {
    val productModel = MutableLiveData<ProductModel>()

    val cartModel = MutableLiveData<CartModel>()

    fun addProductToCart(productId: String){
        val model = productModel.value!!

        FirebaseService.addToCart(productId ,model.price)
    }

    fun addProductToWishlist(productId: String){
        val model = productModel.value!!

        FirebaseService.addToWishlist(productId)
    }

    fun incrementProductQuantity(productId: String){
        val model = productModel.value!!

        FirebaseService.incrementProductQuantity(productId ,model.price)
    }

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