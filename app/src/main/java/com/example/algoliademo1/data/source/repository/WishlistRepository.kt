package com.example.algoliademo1.data.source.repository

import com.example.algoliademo1.ShoppingApplication
import com.example.algoliademo1.data.source.datasource.WishlistDataSource
import com.example.algoliademo1.data.source.local.dao.WishlistDao
import com.example.algoliademo1.data.source.local.localdatasource.WishlistLocalDataSource

class WishlistRepository {

    private val dataSource: WishlistDataSource

    init {
        val dbInstance = ShoppingApplication.instance!!.database
        dataSource = WishlistLocalDataSource(dbInstance.wishlistDao())
    }

    suspend fun addToWishlist(userId: String, productId: String){
        dataSource.addToWishlist(userId, productId)
    }

    suspend fun getWishlist(userId: String): List<String> { // list of product ids
        return dataSource.getWishlist(userId)
    }

    suspend fun removeFromWishlist(userId: String, productId: String){
        dataSource.removeFromWishlist(userId, productId)
    }

    suspend fun isInWishlist(userId: String, productId: String) = dataSource.isInWishlist(userId, productId)

    companion object {
        @Volatile
        private var INSTANCE: WishlistRepository? = null

        fun getRepository(): WishlistRepository {
            return INSTANCE ?: synchronized(this) {
                WishlistRepository().also {
                    INSTANCE = it
                }
            }
        }
    }
}