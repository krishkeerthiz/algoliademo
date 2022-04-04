package com.example.algoliademo1

import android.app.Application
import com.example.algoliademo1.data.source.ShoppingRepository
import com.example.algoliademo1.data.source.local.ShoppingRoomDatabase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class ShoppingApplication: Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { ShoppingRoomDatabase.getDatabase(this.applicationContext, applicationScope) }
//    val userId = Firebase.auth.currentUser!!.uid

//    val repository by lazy { ShoppingRepository(database.productsDao()) }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object{
        var instance : ShoppingApplication? = null
    }
}