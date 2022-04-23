package com.example.algoliademo1.data.source.datasource

import com.example.algoliademo1.data.source.local.entity.Product

interface ProductsDataSource {

    suspend fun getProducts(): List<Product>

    suspend fun getProducts(productIds: List<String>?): List<Product>

    suspend fun getProduct(productId: String): Product

    suspend fun insertProduct(product: Product)

    suspend fun addRating(productId: String, rating: Int)

    suspend fun getUserRating(productId: String): Int?
}