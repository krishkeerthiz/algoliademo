package com.example.algoliademo1.data.source.local.localdatasource

import android.content.ContentValues.TAG
import android.util.Log
import com.example.algoliademo1.data.source.datasource.ProductsDataSource
import com.example.algoliademo1.data.source.local.dao.ProductRatingsDao
import com.example.algoliademo1.data.source.local.dao.ProductsDao
import com.example.algoliademo1.data.source.local.entity.Product
import com.example.algoliademo1.data.source.local.entity.ProductRatings
import com.example.algoliademo1.data.source.remote.FirebaseService

class ProductsLocalDataSource(private val productsDao: ProductsDao, private val productRatingsDao: ProductRatingsDao) : ProductsDataSource {
    override suspend fun getProducts(): List<Product> {
        return productsDao.getProducts()
    }

    override suspend fun getProducts(productIds: List<String>?): List<Product> {
        val products = mutableListOf<Product>()
        if(productIds != null){
            for(productId in productIds){
                products.add(getProduct(productId.removeSurrounding("\"", "\"")))
            }
            Log.d(TAG, "getProducts: wishlist ${products.size}")
        }

        return products
    }

    override suspend fun getProduct(productId: String): Product {
        return productsDao.getProduct(productId)
    }

    override suspend fun insertProduct(product: Product) {
        productsDao.insert(product)
    }

    override suspend fun addRating(productId: String, rating: Int) {
        val userRating = getUserRating(productId)

        if(userRating == null)
        productRatingsDao.insert(ProductRatings(FirebaseService.userId, productId, rating))
        else{
            productRatingsDao.updateRating(FirebaseService.userId, productId, rating)
        }
        val ratings = productRatingsDao.getOverallRating(productId)

        productsDao.updateRating(productId, ratings.average().toInt())
    }

    override suspend fun getUserRating(productId: String): Int? {
        return productRatingsDao.getUserRating(FirebaseService.userId, productId)
    }
}