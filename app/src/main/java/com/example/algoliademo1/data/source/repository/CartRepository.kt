package com.example.algoliademo1.data.source.repository

import com.example.algoliademo1.ShoppingApplication
import com.example.algoliademo1.data.source.datasource.CartDataSource
import com.example.algoliademo1.data.source.local.entity.ItemCount
import com.example.algoliademo1.data.source.local.localdatasource.CartLocalDataSource
import com.example.algoliademo1.data.source.remote.FirebaseService

object CartRepository {

    private val dataSource: CartDataSource

    init {
        val dbInstance = ShoppingApplication.database
        //val dbInstance = ShoppingRoomDatabase.getDatabase()
        dataSource = CartLocalDataSource(
            dbInstance.cartDao(),
            dbInstance.cartItemsDao(),
            dbInstance.productsDao()
        )
    }

    suspend fun addToCart(productId: String, price: Float = 0.0f) { // Price may be needed in remote data source
        dataSource.addToCart(FirebaseService.userId, productId)
    }

    suspend fun removeFromCart(userId: String, productId: String) {
        dataSource.removeFromCart(userId, productId)
    }

    suspend fun incrementItemCount(userId: String, productId: String) {
        dataSource.incrementItemCount(userId, productId)
    }

    suspend fun decrementItemCount(userId: String, productId: String) {
        dataSource.decrementItemCount(userId, productId)
    }

    suspend fun getCartItems(userId: String): List<ItemCount> {
        return dataSource.getCartItems(userId)
    }

    suspend fun getCartTotal(userId: String): Float {
        return dataSource.getCartTotal(userId)
    }

    suspend fun getProductQuantity(userId: String, productId: String): Int {
        return dataSource.getProductQuantity(userId, productId)
    }

    suspend fun emptyCart(userId: String) {
        dataSource.emptyCart(userId)
    }

    suspend fun isProductInCart(userId: String, productId: String) =
        dataSource.isProductInCart(userId, productId)

    suspend fun createCartEntry(userId: String) {
        dataSource.insert(userId)
    }

    suspend fun getCart(userId: String) = dataSource.getCart(userId)

//    companion object {
//        @Volatile
//        private var INSTANCE: CartRepository? = null
//
//        fun getRepository(): CartRepository {
//            return INSTANCE ?: synchronized(this) {
//                CartRepository().also {
//                    INSTANCE = it
//                }
//            }
//        }
//    }
}