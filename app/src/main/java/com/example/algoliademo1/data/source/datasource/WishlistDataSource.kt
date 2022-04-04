package com.example.algoliademo1.data.source.datasource

interface WishlistDataSource {

    suspend fun addToWishlist(userId: String, productId: String)

    suspend fun getWishlist(userId: String): List<String>  // list of product ids

    suspend fun removeFromWishlist(userId: String, productId: String)
}