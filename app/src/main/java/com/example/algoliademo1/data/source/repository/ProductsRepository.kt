package com.example.algoliademo1.data.source.repository

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.algoliademo1.ShoppingApplication
import com.example.algoliademo1.data.source.datasource.ProductsDataSource
import com.example.algoliademo1.data.source.local.entity.Product
import com.example.algoliademo1.data.source.local.localdatasource.ProductsLocalDataSource
import com.example.algoliademo1.data.source.remote.remotedatasource.ProductRemoteDataSource
import com.example.algoliademo1.model.ProductModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ProductsRepository {

    private val dataSource: ProductsDataSource
    private val remoteDataSource: ProductRemoteDataSource

    suspend fun getProducts(): List<Product> = dataSource.getProducts()

    suspend fun getProducts(productIds: List<String>?): List<Product?> =
        dataSource.getProducts(productIds)

    suspend fun getProductsSize() = withContext(Dispatchers.IO){
        return@withContext dataSource.getProductsSize()
    }

    suspend fun getProduct(productId: String): Product? {
        val product =
            withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                dataSource.getProduct(productId)
            }
        return product
    }

    suspend fun addRating(productId: String, rating: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            dataSource.addRating(productId, rating)
        }
    }

    suspend fun getUserRating(productId: String): Int? {
        val rating =
            withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                dataSource.getUserRating(productId)
            }
        return rating
    }

    suspend fun addProductToLocal(productModel: ProductModel, dbSize: Int){
        dataSource.insertProduct(productModelToProduct(productModel, dbSize))
    }

    fun addProductToRemote(productModel: ProductModel): LiveData<Boolean?> {

        Log.d(TAG, "upload: addProductToRemote: ")
        return remoteDataSource.addProduct("6227", productModel)

    }

    private fun productModelToProduct(productModel: ProductModel, dbSize: Int): Product {
//        var dbSize = 0
//
//        CoroutineScope(Dispatchers.IO).launch {
//            dbSize = dataSource.getProductsSize()
//        }.join()

        return Product(
            dbSize.toString(),
            productModel.brand,
            productModel.description,
            productModel.free_shipping,
            productModel.image,
            productModel.name,
            productModel.objectId,
            productModel.popularity,
            productModel.price,
            productModel.price_range,
            productModel.rating,
            productModel.type,
            productModel.url
        )
    }

    init {
        val dbInstance = ShoppingApplication.database
//        val dbInstance = ShoppingRoomDatabase.getDatabase()
        dataSource =
            ProductsLocalDataSource(dbInstance.productsDao(), dbInstance.productRatingsDao())

        remoteDataSource = ProductRemoteDataSource()
    }

//    companion object {
//        @Volatile
//        private var INSTANCE: ProductsRepository? = null
//
//        fun getRepository(): ProductsRepository {
//            return INSTANCE ?: synchronized(this) {
//                ProductsRepository().also {
//                    INSTANCE = it
//                }
//            }
//        }
//    }
}