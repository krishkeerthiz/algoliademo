package com.example.algoliademo1.data.source.local.localdatasource

import com.example.algoliademo1.data.source.datasource.AddressDataSource
import com.example.algoliademo1.data.source.local.dao.AddressDao
import com.example.algoliademo1.data.source.local.dao.AddressListDao
import com.example.algoliademo1.data.source.local.entity.Address
import com.example.algoliademo1.data.source.local.entity.AddressList
import com.example.algoliademo1.model.AddressModel

class AddressLocalDataSource(val addressDao: AddressDao, val addressListDao: AddressListDao) :
    AddressDataSource {

    override suspend fun addAddress(userId: String, addressId: String, addressModel: AddressModel) {
        // Adding address
        val address = Address(
            addressId,
            addressModel.address,
            addressModel.city,
            addressModel.doorNumber,
            addressModel.pincode,
            addressModel.state
        )
        addressDao.insert(address)

        // Adding address list
        addressListDao.insert(AddressList(userId, addressId))

    }

    override suspend fun getAddressList(userId: String): List<Address> {
        val addressList = mutableListOf<Address>()

        val addressIds = addressListDao.getAddresses(userId)

        for (id in addressIds)
            addressList.add(addressDao.getAddress(id))

        return addressList
    }

    //return addressListDao.getUserAddresses(userId)
    override suspend fun getAddress(addressId: String, userId: String): Address {
        return addressDao.getAddress(addressId)
    }
}