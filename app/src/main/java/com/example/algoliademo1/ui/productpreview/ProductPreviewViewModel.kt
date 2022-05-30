package com.example.algoliademo1.ui.productpreview

import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.algolia.search.model.IndexName
import com.example.algoliademo1.data.source.remote.AlgoliaIndex
import com.example.algoliademo1.data.source.repository.ProductsRepository
import com.example.algoliademo1.data.source.repository.StorageRepository
import com.example.algoliademo1.model.ProductModel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.*

class ProductPreviewViewModel(context: Context, private val productModel: ProductModel, private val uri: Uri) :
    ViewModel() {

    private val storageRepository = StorageRepository
    private val productRepository = ProductsRepository.getRepository(context)

    fun uploadImage(): MutableLiveData<String?> {
        Log.d(TAG, "uploadImage: called")

        return storageRepository.uploadImage(uri)

    }

    fun uploadProduct(url: String): LiveData<Boolean?> {
        Log.d(TAG, "uploadProduct: started")
        productModel.image = url

        //var dbSize: Int

        viewModelScope.launch {
            val dbSize = productRepository.getProductsSize()

            productRepository.addProductToLocal(productModel, dbSize)

            val index = AlgoliaIndex.getClient().initIndex(IndexName("products4"))

            index.saveObject(productModelToJson(productModel, dbSize))
        }

        return productRepository.addProductToRemote(productModel)
    }

    private fun productModelToJson(productModel: ProductModel, dbSize: Int): JsonObject {
        val json = buildJsonObject {
            put("objectID", dbSize.toString())
            put("name", productModel.name)
            put("brand", productModel.brand)
            putJsonArray("categories") {
                add(productModel.categories.toString())
            }
            put("free_shipping", productModel.free_shipping)
            put("price_range", productModel.free_shipping)
            put("type", productModel.type)
            put("description", productModel.description)
            put("price", productModel.price)
            put("rating", productModel.rating)
        }
        return json
    }

}

class ProductPreviewViewModelFactory(private val context: Context, private val productModel: ProductModel, private val uri: Uri) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProductPreviewViewModel(context, productModel, uri) as T
    }
}