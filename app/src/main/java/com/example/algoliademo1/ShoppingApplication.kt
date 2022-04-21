package com.example.algoliademo1

import android.app.Application
import com.example.algoliademo1.data.source.local.ShoppingRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class ShoppingApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob())

    lateinit var database: ShoppingRoomDatabase

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = ShoppingRoomDatabase.getDatabase(this.applicationContext, applicationScope)
    }

    companion object {
        lateinit var instance: ShoppingApplication
    }
}