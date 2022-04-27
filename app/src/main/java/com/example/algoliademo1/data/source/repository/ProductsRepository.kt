package com.example.algoliademo1.data.source.repository

import com.example.algoliademo1.ShoppingApplication
import com.example.algoliademo1.data.source.datasource.ProductsDataSource
import com.example.algoliademo1.data.source.local.entity.Product
import com.example.algoliademo1.data.source.local.localdatasource.ProductsLocalDataSource
import kotlinx.coroutines.*

class ProductsRepository {

    private val dataSource: ProductsDataSource

    suspend fun getProducts(): List<Product> = dataSource.getProducts()

    suspend fun getProducts(productIds: List<String>?): List<Product?> =
        dataSource.getProducts(productIds)

    suspend fun getProduct(productId: String): Product? {
        val product =
            withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                dataSource.getProduct(productId)
            }
        return product
    }

    suspend fun addRating(productId: String, rating: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            dataSource.addRating(productId, rating)
        }
    }

    suspend fun getUserRating(productId: String): Int? {
        val rating =
            withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                dataSource.getUserRating(productId)
            }
        return rating
    }

    init {
        val dbInstance = ShoppingApplication.instance.database
        dataSource =
            ProductsLocalDataSource(dbInstance.productsDao(), dbInstance.productRatingsDao())
    }

    companion object {
        @Volatile
        private var INSTANCE: ProductsRepository? = null

        fun getRepository(): ProductsRepository {
            return INSTANCE ?: synchronized(this) {
                ProductsRepository().also {
                    INSTANCE = it
                }
            }
        }
    }
}