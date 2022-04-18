package com.example.algoliademo1.data.source.datasource

import com.example.algoliademo1.data.source.local.entity.Product

interface ProductsDataSource {

    suspend fun getProducts(): List<Product>

    suspend fun getProduct(productId: String): Product

    suspend fun insertProduct(product: Product)
}