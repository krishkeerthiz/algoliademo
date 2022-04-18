package com.example.algoliademo1.data.source.remote.remotedatasource

import com.example.algoliademo1.data.source.datasource.AddressDataSource
import com.example.algoliademo1.data.source.local.entity.Address
import com.example.algoliademo1.model.AddressModel

class AddressRemoteDataSource : AddressDataSource {
    override suspend fun addAddress(userId: String, addressId: String, addressModel: AddressModel) {
        TODO("Not yet implemented")
    }

    override suspend fun getAddressList(userId: String): List<Address> {
        TODO("Not yet implemented")
    }

    override suspend fun getAddress(addressId: String, userId: String): Address {
        TODO("Not yet implemented")
//        val addressReference = FirebaseService.getAddressReference()
//        //val addressDocumentReference =
//        val addressSnapshot  =addressReference.document(addressId).get().await()
//
//        val address = addressSnapshot.toObject<AddressModel>()!!
//
//        return address
    }
}