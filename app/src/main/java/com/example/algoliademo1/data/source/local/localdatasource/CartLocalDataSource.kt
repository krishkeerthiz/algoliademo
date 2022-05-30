package com.example.algoliademo1.data.source.local.localdatasource

import com.example.algoliademo1.data.source.datasource.CartDataSource
import com.example.algoliademo1.data.source.local.dao.CartDao
import com.example.algoliademo1.data.source.local.dao.CartItemsDao
import com.example.algoliademo1.data.source.local.dao.ProductsDao
import com.example.algoliademo1.data.source.local.entity.Cart
import com.example.algoliademo1.data.source.local.entity.CartItems
import com.example.algoliademo1.model.ItemCountModel

class CartLocalDataSource(
    private val cartDao: CartDao,
    private val cartItemsDao: CartItemsDao,
    private val productsDao: ProductsDao
) : CartDataSource {

    override suspend fun addToCart(userId: String, productId: String){
        cartItemsDao.insert(CartItems(userId, productId, 1))
        addItemPrice(userId, productId)
    }

    override suspend fun removeFromCart(userId: String, productId: String) {
            val quantity = cartItemsDao.getProductQuantity(userId, productId)

            if(quantity != null) {
                cartItemsDao.deleteProduct(userId, productId)
                reduceItemPrice(userId, productId, quantity)
            }
        }

    override suspend fun incrementItemCount(userId: String, productId: String) {
            val productQuantity = cartItemsDao.getProductQuantity(userId, productId)

            if(productQuantity != null){
                cartItemsDao.updateProductQuantity(userId, productId, productQuantity + 1)
                addItemPrice(userId, productId)
            }

        }

    private suspend fun addItemPrice(userId: String, productId: String) {
        val total = cartDao.getCartTotal(userId)
        val newTotal = total + productsDao.getProductPrice(productId)
        cartDao.updateCartTotal(userId, newTotal)
    }

    override suspend fun decrementItemCount(userId: String, productId: String) {
            val productQuantity = cartItemsDao.getProductQuantity(userId, productId)

            if (productQuantity != null && productQuantity > 0) {
                cartItemsDao.updateProductQuantity(userId, productId, productQuantity - 1)
                reduceItemPrice(userId, productId)
            }
        }

    private suspend fun reduceItemPrice(userId: String, productId: String, count: Int = 1) {
        val total = cartDao.getCartTotal(userId)
        val newTotal = total - (productsDao.getProductPrice(productId) * count)
        cartDao.updateCartTotal(userId, newTotal)
    }

    override suspend fun getCartItems(userId: String): List<ItemCountModel> = cartItemsDao.getProductsAndQuantities(userId)

    override suspend fun getProductQuantity(userId: String, productId: String): Int? = cartItemsDao.getProductQuantity(userId, productId)

    override suspend fun getCartTotal(userId: String): Float = cartDao.getCartTotal(userId)

    override suspend fun emptyCart(userId: String) {
        cartItemsDao.deleteProducts(userId)
        cartDao.updateCartTotal(userId, 0.0f)
    }

    override suspend fun isProductInCart(userId: String, productId: String): Boolean =
        cartItemsDao.isProductInCart(userId, productId) != null

    override suspend fun insert(userId: String) {
        cartDao.insert(Cart(userId, 0.0f))
    }

    override suspend fun getCart(userId: String): Cart? {
        return cartDao.getCart(userId)
    }
}