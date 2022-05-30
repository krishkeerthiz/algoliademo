package com.example.algoliademo1.data.source.repository

import android.content.Context
import com.example.algoliademo1.data.source.datasource.AddressDataSource
import com.example.algoliademo1.data.source.local.ShoppingRoomDatabase
import com.example.algoliademo1.data.source.local.localdatasource.AddressLocalDataSource
import com.example.algoliademo1.model.AddressModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddressRepository(context: Context) {

    private val dataSource: AddressDataSource

    init {
        val dbInstance = ShoppingRoomDatabase.getDatabase(context, CoroutineScope(Dispatchers.IO))
        dataSource = AddressLocalDataSource(dbInstance.addressDao(), dbInstance.addressListDao())
    }

    suspend fun addAddress(userId: String, addressId: String, addressModel: AddressModel) {
        CoroutineScope(Dispatchers.IO).launch {
            dataSource.addAddress(userId, addressId, addressModel)
        }.join() // add this if you want other execution to take place after this

    }

    suspend fun getAddressList(userId: String) = withContext(Dispatchers.IO) {
        dataSource.getAddressList(userId)
    }

    suspend fun getAddress(addressId: String, userId: String) = withContext(Dispatchers.IO) {
        dataSource.getAddress(addressId, userId)
    }

//    suspend fun addAddress(userId: String, addressId: String, addressModel: AddressModel) {
//        dataSource.addAddress(userId, addressId, addressModel)
//    }
//
//    suspend fun getAddressList(userId: String): List<Address> {
//        return dataSource.getAddressList(userId)
//    }
//
//    suspend fun getAddress(addressId: String, userId: String): Address {
//        return dataSource.getAddress(addressId, userId)
//    }

    companion object {
        @Volatile
        private var INSTANCE: AddressRepository? = null

        fun getRepository(context: Context): AddressRepository {
            return INSTANCE ?: synchronized(this) {
                AddressRepository(context).also {
                    INSTANCE = it
                }
            }
        }
    }
}