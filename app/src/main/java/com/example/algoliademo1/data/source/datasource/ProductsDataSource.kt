package com.example.algoliademo1.data.source.datasource

import androidx.paging.PagedList
import com.example.algoliademo1.data.source.local.entity.Product
import kotlinx.coroutines.flow.Flow

interface ProductsDataSource {

    suspend fun getProducts(): List<Product>

    suspend fun getProduct(productId: String): Product

    suspend fun insertProduct(product: Product)
}