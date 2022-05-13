package com.example.algoliademo1.data.source.repository

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.storage.FirebaseStorage
import java.util.*

object StorageRepository {

    private val storage = FirebaseStorage.getInstance()
    private val storageReference = storage.reference

    fun uploadImage(filePath: Uri): MutableLiveData<String?> {
        val mutableLiveData = MutableLiveData<String?>()
        var downloadUrl: String?
        // Defining the child of storageReference
        val ref = storageReference.child(
            "images/"
                    + UUID.randomUUID().toString() + ".jpeg"
        )

        val uploadTask = ref.putFile(filePath)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            Log.d(TAG, "uploadImage: on complete listener")

            downloadUrl = if (task.isSuccessful) {
                Log.d(TAG, "uploadImage: on success")
                task.result.toString()
            } else {
                Log.d(TAG, "uploadImage: on failure")
                null
            }

            mutableLiveData.value = downloadUrl

        }

        return mutableLiveData
    }
}