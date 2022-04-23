package com.example.algoliademo1.data.source.local.localdatasource

import com.example.algoliademo1.data.source.datasource.WishlistDataSource
import com.example.algoliademo1.data.source.local.dao.WishlistDao
import com.example.algoliademo1.data.source.local.entity.Wishlist
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WishlistLocalDataSource(private val wishlistDao: WishlistDao) : WishlistDataSource {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun addToWishlist(userId: String, productId: String) =
        withContext(ioDispatcher) {
            wishlistDao.insert(Wishlist(userId, productId))
        }

    override suspend fun getWishlist(userId: String): List<String> = withContext(ioDispatcher) {
        return@withContext wishlistDao.getItems(userId)
    }

    override suspend fun removeFromWishlist(userId: String, productId: String) =
        withContext(ioDispatcher) {
            wishlistDao.deleteProduct(userId, productId)
        }

    override suspend fun isInWishlist(userId: String, productId: String): Boolean {
        return wishlistDao.getItem(userId, productId) != null
    }

}