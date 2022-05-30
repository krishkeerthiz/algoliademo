package com.example.algoliademo1.data.source.datasource

import com.example.algoliademo1.data.source.local.entity.Address
import com.example.algoliademo1.model.AddressModel

interface AddressDataSource {

    suspend fun addAddress(userId: String, addressId: String, addressModel: AddressModel)  // AddressModel model does not contains address id

    suspend fun getAddressList(userId: String): List<Address> // Address model contains address id also

    suspend fun getAddress(addressId: String, userId: String): Address

}