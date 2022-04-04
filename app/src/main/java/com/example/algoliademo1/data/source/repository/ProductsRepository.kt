package com.example.algoliademo1.data.source.repository

import android.app.Application
import androidx.annotation.WorkerThread
import com.example.algoliademo1.ShoppingApplication
import com.example.algoliademo1.data.source.datasource.ProductsDataSource
import com.example.algoliademo1.data.source.local.entity.Product
import com.example.algoliademo1.data.source.local.localdatasource.ProductsLocalDataSource
import kotlinx.coroutines.flow.Flow

class ProductsRepository {

    private val dataSource: ProductsDataSource

    fun getProducts(): Flow<List<Product>> = dataSource.getProducts()

    fun getProduct(productId: String) = dataSource.getProduct(productId)

    //suspend fun searchResult(searchText: String) = productsDao.getSearchProducts(searchText)

    @Suppress("RedundantSuspendModifier")
    //@WorkerThread
    suspend fun addProduct(product: Product){
        dataSource.insertProduct(product)
    }

    init {
        val dbInstance = ShoppingApplication.instance?.database

        dataSource = ProductsLocalDataSource(dbInstance!!.productsDao())

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