package com.example.algoliademo1.data.source.remote.remotedatasource

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.algoliademo1.model.ProductModel
import com.google.firebase.firestore.FirebaseFirestore

class ProductRemoteDataSource {

    fun addProduct(productId: String, productModel: ProductModel): MutableLiveData<Boolean?> {
        Log.d(TAG, "upload addProduct: ")

        val db = FirebaseFirestore.getInstance()
        val result = MutableLiveData<Boolean?>()

        result.value = null
        val documentRef = db.collection("products4").document(productId)

        documentRef.set(productModel)
            .addOnSuccessListener {
                result.value = true
            }
            .addOnFailureListener {
                result.value = false
            }

        return result
    }
}