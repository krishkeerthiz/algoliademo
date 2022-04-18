package com.example.algoliademo1.data.source.datasource

import com.example.algoliademo1.data.source.local.entity.Cart
import com.example.algoliademo1.data.source.local.entity.ItemCount

interface CartDataSource {

    suspend fun addToCart(userId: String, productId: String)

    suspend fun removeFromCart(userId: String, productId: String)

    suspend fun incrementItemCount(userId: String, productId: String)

    suspend fun decrementItemCount(userId: String, productId: String)

    suspend fun getCartItems(userId: String): List<ItemCount>  // Map<productId, quantity>

    suspend fun getProductQuantity(userId: String, productId: String): Int

   // fun getCart(userId: String): Cart

    suspend fun getCartTotal(userId: String): Float

    suspend fun emptyCart(userId: String)

    suspend fun isProductInCart(userId: String, productId: String): Boolean

    suspend fun insert(userId: String)

    suspend fun getCart(userId: String): Cart?
}