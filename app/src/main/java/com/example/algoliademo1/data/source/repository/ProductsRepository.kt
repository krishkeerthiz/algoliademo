package com.example.algoliademo1.data.source.repository

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.annotation.WorkerThread
import com.example.algoliademo1.ShoppingApplication
import com.example.algoliademo1.data.source.datasource.ProductsDataSource
import com.example.algoliademo1.data.source.local.entity.Product
import com.example.algoliademo1.data.source.local.localdatasource.ProductsLocalDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow

class ProductsRepository {

    private val dataSource: ProductsDataSource

    suspend fun getProducts(): List<Product> = dataSource.getProducts()

    suspend fun getProduct(productId: String): Product{
        val product =CoroutineScope(Dispatchers.IO).async {
            dataSource.getProduct(productId)
        }.await()
        Log.d(TAG, "getProduct: $product  $productId  ${productId.length}")
        return product
    }

    //suspend fun searchResult(searchText: String) = productsDao.getSearchProducts(searchText)


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