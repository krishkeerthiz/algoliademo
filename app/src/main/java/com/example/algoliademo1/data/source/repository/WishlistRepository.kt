package com.example.algoliademo1.data.source.repository

import android.content.Context
import com.example.algoliademo1.data.source.datasource.WishlistDataSource
import com.example.algoliademo1.data.source.local.ShoppingRoomDatabase
import com.example.algoliademo1.data.source.local.localdatasource.WishlistLocalDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WishlistRepository(context: Context) {

    private val dataSource: WishlistDataSource

    init {
        val dbInstance = ShoppingRoomDatabase.getDatabase(context, CoroutineScope(IO))
        dataSource = WishlistLocalDataSource(dbInstance.wishlistDao())
    }

    suspend fun addToWishlist(userId: String, productId: String) {
        CoroutineScope(IO).launch {
            dataSource.addToWishlist(userId, productId)
        }
            .join()  // Waits till the launch execution completes, without this inconsistent UI actions happened
    }

    suspend fun getWishlist(userId: String): List<String> = withContext(IO) { // list of product ids
        dataSource.getWishlist(userId)
    }

    suspend fun removeFromWishlist(userId: String, productId: String) {
        CoroutineScope(IO).launch {
            dataSource.removeFromWishlist(userId, productId)
        }.join()
    }

    suspend fun isInWishlist(userId: String, productId: String) = withContext(IO) {
        dataSource.isInWishlist(userId, productId)
    }

    companion object {
        @Volatile
        private var INSTANCE: WishlistRepository? = null

        fun getRepository(context: Context): WishlistRepository {
            return INSTANCE ?: synchronized(this) {
                WishlistRepository(context).also {
                    INSTANCE = it
                }
            }
        }
    }
}