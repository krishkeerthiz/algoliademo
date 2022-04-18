package com.example.algoliademo1.data.source.local.localdatasource

import android.content.ContentValues.TAG
import android.util.Log
import com.example.algoliademo1.data.source.datasource.CartDataSource
import com.example.algoliademo1.data.source.local.dao.CartDao
import com.example.algoliademo1.data.source.local.dao.CartItemsDao
import com.example.algoliademo1.data.source.local.dao.ProductsDao
import com.example.algoliademo1.data.source.local.entity.Cart
import com.example.algoliademo1.data.source.local.entity.CartItems
import com.example.algoliademo1.data.source.local.entity.ItemCount
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CartLocalDataSource(
    val cartDao: CartDao,
    val cartItemsDao: CartItemsDao,
    val productsDao: ProductsDao
) : CartDataSource {
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun addToCart(userId: String, productId: String) = withContext(ioDispatcher) {
        val id = cartItemsDao.insert(CartItems(userId, productId, 1))
        Log.d(TAG, "addToCart: $id")
        addItemPrice(userId, productId)
    }

    override suspend fun removeFromCart(userId: String, productId: String) =
        withContext(Dispatchers.IO) {
            val quantity = cartItemsDao.getProductQuantity(userId, productId)
            cartItemsDao.deleteProduct(userId, productId)
            reduceItemPrice(userId, productId, quantity)
        }

    override suspend fun incrementItemCount(userId: String, productId: String) =
        withContext(Dispatchers.IO) {
            val productQuantity = cartItemsDao.getProductQuantity(userId, productId)
            cartItemsDao.updateProductQuantity(userId, productId, productQuantity + 1)

            addItemPrice(userId, productId)
        }

    private fun addItemPrice(userId: String, productId: String) {
        val total = cartDao.getCartTotal(userId)
        val newTotal = total + productsDao.getProductPrice(productId)
        cartDao.updateCartTotal(userId, newTotal)
    }

    override suspend fun decrementItemCount(userId: String, productId: String) =
        withContext(Dispatchers.IO) {
            val productQuantity = cartItemsDao.getProductQuantity(userId, productId)

            if (productQuantity > 0) {
                cartItemsDao.updateProductQuantity(userId, productId, productQuantity - 1)
                reduceItemPrice(userId, productId)
            }
        }

    private fun reduceItemPrice(userId: String, productId: String, count: Int = 1) {
        val total = cartDao.getCartTotal(userId)
        val newTotal = total - (productsDao.getProductPrice(productId) * count)
        cartDao.updateCartTotal(userId, newTotal)
    }

    override suspend fun getCartItems(userId: String): List<ItemCount> = withContext(ioDispatcher) {
        return@withContext cartItemsDao.getProductsAndQuantities(userId)
    }

    override suspend fun getProductQuantity(userId: String, productId: String): Int =
        withContext(ioDispatcher) {
            return@withContext cartItemsDao.getProductQuantity(userId, productId)
        }

    override suspend fun getCartTotal(userId: String): Float = withContext(ioDispatcher) {
        return@withContext cartDao.getCartTotal(userId)
    }

    override suspend fun emptyCart(userId: String) = withContext(ioDispatcher) {
        cartItemsDao.deleteProducts(userId)
        cartDao.updateCartTotal(userId, 0.0f)
    }

    override suspend fun isProductInCart(userId: String, productId: String): Boolean =
        withContext(ioDispatcher) {
            return@withContext cartItemsDao.isProductInCart(userId, productId) != null
        }

    override suspend fun insert(userId: String) {
        cartDao.insert(Cart(userId, 0.0f))
    }

    override suspend fun getCart(userId: String): Cart? {
        return cartDao.getCart(userId)
    }
}