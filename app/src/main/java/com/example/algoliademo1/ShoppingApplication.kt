package com.example.algoliademo1

import android.app.Application
import com.example.algoliademo1.data.source.local.ShoppingRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class ShoppingApplication : Application() {

//    private val applicationScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
       // instance = this
        database = ShoppingRoomDatabase.getDatabase(this.applicationContext, CoroutineScope(Dispatchers.IO))

    }

    companion object {
//        lateinit var instance: ShoppingApplication
//        private set
        lateinit var database: ShoppingRoomDatabase
        private set
    }
}