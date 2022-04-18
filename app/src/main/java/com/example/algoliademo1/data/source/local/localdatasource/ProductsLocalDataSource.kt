package com.example.algoliademo1.data.source.local.localdatasource

import com.example.algoliademo1.data.source.datasource.ProductsDataSource
import com.example.algoliademo1.data.source.local.dao.ProductsDao
import com.example.algoliademo1.data.source.local.entity.Product

class ProductsLocalDataSource(val productsDao: ProductsDao) : ProductsDataSource {
    override suspend fun getProducts(): List<Product> {
        return productsDao.getProducts()
    }

    override suspend fun getProduct(productId: String): Product {
        return productsDao.getProduct(productId)
    }

    override suspend fun insertProduct(product: Product) {
        productsDao.insert(product)
    }
}