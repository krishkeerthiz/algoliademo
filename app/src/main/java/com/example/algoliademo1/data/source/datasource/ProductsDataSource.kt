package com.example.algoliademo1.data.source.datasource

import androidx.paging.PagedList
import com.example.algoliademo1.data.source.local.entity.Product
import kotlinx.coroutines.flow.Flow

interface ProductsDataSource {

    fun getProducts(): Flow<List<Product>>

    fun getProduct(productId: String): Flow<List<Product>>

    suspend fun insertProduct(product: Product)
}