package com.example.algoliademo1.data.source.datasource

import com.example.algoliademo1.data.source.local.entity.Cart
import com.example.algoliademo1.model.ItemCountModel

interface CartDataSource {

    suspend fun addToCart(userId: String, productId: String)

    suspend fun removeFromCart(userId: String, productId: String)

    suspend fun incrementItemCount(userId: String, productId: String)

    suspend fun decrementItemCount(userId: String, productId: String)

    suspend fun getCartItems(userId: String): List<ItemCountModel>

    suspend fun getProductQuantity(userId: String, productId: String): Int?

    suspend fun getCartTotal(userId: String): Float

    suspend fun emptyCart(userId: String)

    suspend fun isProductInCart(userId: String, productId: String): Boolean

    suspend fun insert(userId: String)  // initializes cart for user

    suspend fun getCart(userId: String): Cart?  // When cart is null, it means new user so create cart for them
}