package com.example.algoliademo1.data.source.local.localdatasource

import com.example.algoliademo1.data.source.datasource.ProductsDataSource
import com.example.algoliademo1.data.source.local.dao.ProductsDao
import com.example.algoliademo1.data.source.local.entity.Product
import kotlinx.coroutines.flow.Flow

class ProductsLocalDataSource(val productsDao: ProductsDao): ProductsDataSource {
    override fun getProducts(): Flow<List<Product>> {
        return productsDao.getProducts()
    }

    override fun getProduct(productId: String): Flow<List<Product>> {
        return productsDao.getProduct(productId)
    }

    override suspend fun insertProduct(product: Product) {
        productsDao.insert(product)
    }
}