package com.example.algoliademo1.data.source

import androidx.annotation.WorkerThread
import com.example.algoliademo1.data.source.local.dao.ProductsDao
import com.example.algoliademo1.data.source.local.entity.Product
import kotlinx.coroutines.flow.Flow

class ShoppingRepository(private val productsDao: ProductsDao) {

    val allProducts: Flow<List<Product>> = productsDao.getProducts()

    fun getProduct(productId: String) = productsDao.getProduct(productId)

    fun searchResult(searchText: String) = productsDao.getSearchProducts(searchText)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun addProduct(product: Product){
        productsDao.insert(product)
    }

}