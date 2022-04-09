package com.example.algoliademo1

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
//import com.example.algoliademo1.data.source.ShoppingRepository
import com.example.algoliademo1.data.source.local.ShoppingRoomDatabase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*

class ShoppingApplication: Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { ShoppingRoomDatabase.getDatabase(this.applicationContext, applicationScope) }
    //val database = ShoppingRoomDatabase.getDatabase(this.applicationContext, applicationScope)
//    val userId = Firebase.auth.currentUser!!.uid

//    val repository by lazy { ShoppingRepository(database.productsDao()) }

    override fun onCreate() {
        super.onCreate()
        instance = this
//        CoroutineScope(Dispatchers.IO).launch {
//            Log.d(TAG, "Application onCreate : ${database.productsDao().getProducts()}")
//        }

    }

    companion object{
        var instance : ShoppingApplication? = null
    }
}