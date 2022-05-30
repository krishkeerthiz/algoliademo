package com.example.algoliademo1.data.source.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.algoliademo1.data.source.datasource.ProductsDataSource
import com.example.algoliademo1.data.source.local.ShoppingRoomDatabase
import com.example.algoliademo1.data.source.local.entity.Product
import com.example.algoliademo1.data.source.local.localdatasource.ProductsLocalDataSource
import com.example.algoliademo1.data.source.remote.remotedatasource.ProductRemoteDataSource
import com.example.algoliademo1.model.ProductModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductsRepository(context: Context) {

    private val dataSource: ProductsDataSource
    private val remoteDataSource: ProductRemoteDataSource

    init {
        val dbInstance = ShoppingRoomDatabase.getDatabase(context, CoroutineScope(IO))
        dataSource =
            ProductsLocalDataSource(dbInstance.productsDao(), dbInstance.productRatingsDao())

        remoteDataSource = ProductRemoteDataSource()
    }

    suspend fun getProducts(): List<Product> = withContext(IO) { dataSource.getProducts() }

    suspend fun getProducts(productIds: List<String>?): List<Product?> = withContext(IO) {
        dataSource.getProducts(productIds)
    }

    suspend fun getProductsSize() = withContext(IO) {
        dataSource.getProductsSize()
    }

    suspend fun getProduct(productId: String): Product? = withContext(IO) {
        dataSource.getProduct(productId)
    }

    suspend fun addRating(productId: String, rating: Int) {
        CoroutineScope(IO).launch {
            dataSource.addRating(productId, rating)
        }.join()
    }

    suspend fun getUserRating(productId: String): Int? = withContext(IO) {
        dataSource.getUserRating(productId)
    }

    suspend fun addProductToLocal(productModel: ProductModel, dbSize: Int) {
        CoroutineScope(IO).launch {
            dataSource.insertProduct(productModelToProduct(productModel, dbSize))
        }.join()
    }

    fun addProductToRemote(productModel: ProductModel): LiveData<Boolean?> {
        return remoteDataSource.addProduct("6227", productModel)
    }

    private fun productModelToProduct(productModel: ProductModel, dbSize: Int): Product {
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


    companion object {
        @Volatile
        private var INSTANCE: ProductsRepository? = null

        fun getRepository(context: Context): ProductsRepository {
            return INSTANCE ?: synchronized(this) {
                ProductsRepository(context).also {
                    INSTANCE = it
                }
            }
        }
    }
}