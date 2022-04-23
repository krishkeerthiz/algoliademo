package com.example.algoliademo1.data.source.datasource

import com.example.algoliademo1.data.source.local.entity.Address
import com.example.algoliademo1.model.AddressModel

interface AddressDataSource {

    suspend fun addAddress(userId: String, addressId: String, addressModel: AddressModel)

    suspend fun getAddressList(userId: String): List<Address>

    suspend fun getAddress(addressId: String, userId: String): Address

}