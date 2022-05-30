package com.example.algoliademo1.data.source.repository

import android.content.Context
import com.example.algoliademo1.data.source.datasource.CartDataSource
import com.example.algoliademo1.data.source.local.ShoppingRoomDatabase
import com.example.algoliademo1.data.source.local.localdatasource.CartLocalDataSource
import com.example.algoliademo1.data.source.remote.FirebaseService
import com.example.algoliademo1.model.ItemCountModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartRepository(context: Context) {

    private val dataSource: CartDataSource

    init {
        val dbInstance = ShoppingRoomDatabase.getDatabase(context, CoroutineScope(IO))
        dataSource = CartLocalDataSource(
            dbInstance.cartDao(),
            dbInstance.cartItemsDao(),
            dbInstance.productsDao()
        )
    }

    suspend fun addToCart(
        productId: String,
        price: Float = 0.0f
    ) {  // Price may be needed in remote data source
        CoroutineScope(IO).launch {
            dataSource.addToCart(FirebaseService.userId, productId)
        }.join()
    }

    suspend fun removeFromCart(userId: String, productId: String) {
        CoroutineScope(IO).launch {
            dataSource.removeFromCart(userId, productId)
        }.join()
    }

    suspend fun incrementItemCount(userId: String, productId: String) {
        CoroutineScope(IO).launch {
            dataSource.incrementItemCount(userId, productId)
        }.join()  // without join incorrect execution
    }

    suspend fun decrementItemCount(userId: String, productId: String) {
        CoroutineScope(IO).launch {
            dataSource.decrementItemCount(userId, productId)
        }.join()
    }

    suspend fun getCartItems(userId: String): List<ItemCountModel> = withContext(IO) {
        dataSource.getCartItems(userId)
    }

    suspend fun getCartTotal(userId: String): Float = withContext(IO) {
        dataSource.getCartTotal(userId)
    }

    suspend fun getProductQuantity(userId: String, productId: String): Int? = withContext(IO) {
        dataSource.getProductQuantity(userId, productId)
    }

    suspend fun emptyCart(userId: String) {
        CoroutineScope(IO).launch {
            dataSource.emptyCart(userId)
        }.join()
    }

    suspend fun isProductInCart(userId: String, productId: String) = withContext(IO) {
        dataSource.isProductInCart(userId, productId)
    }

    suspend fun createCartEntry(userId: String) {
        CoroutineScope(IO).launch {
            dataSource.insert(userId)
        }.join()
    }

    suspend fun getCart(userId: String) = withContext(IO) {
        dataSource.getCart(userId)
    }

    companion object {
        @Volatile
        private var INSTANCE: CartRepository? = null

        fun getRepository(context: Context): CartRepository {
            return INSTANCE ?: synchronized(this) {
                CartRepository(context).also {
                    INSTANCE = it
                }
            }
        }
    }
}