package com.example.algoliademo1.data.source.repository

import com.example.algoliademo1.ShoppingApplication
import com.example.algoliademo1.data.source.datasource.AddressDataSource
import com.example.algoliademo1.data.source.local.entity.Address
import com.example.algoliademo1.data.source.local.localdatasource.AddressLocalDataSource
import com.example.algoliademo1.model.AddressModel

object AddressRepository {

    private val dataSource: AddressDataSource

    init {
        val dbInstance = ShoppingApplication.database
        //val dbInstance = ShoppingRoomDatabase.getDatabase()
        dataSource = AddressLocalDataSource(dbInstance.addressDao(), dbInstance.addressListDao())
    }

    suspend fun addAddress(userId: String, addressId: String, addressModel: AddressModel) {
        dataSource.addAddress(userId, addressId, addressModel)
    }

    suspend fun getAddressList(userId: String): List<Address> {
        return dataSource.getAddressList(userId)
    }

    suspend fun getAddress(addressId: String, userId: String): Address {
        return dataSource.getAddress(addressId, userId)
    }

//    companion object {
//        @Volatile
//        private var INSTANCE: AddressRepository? = null
//
//        fun getRepository(): AddressRepository {
//            return INSTANCE ?: synchronized(this) {
//                AddressRepository().also {
//                    INSTANCE = it
//                }
//            }
//        }
//    }
}