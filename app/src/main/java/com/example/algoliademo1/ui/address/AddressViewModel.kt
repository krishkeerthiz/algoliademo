package com.example.algoliademo1.ui.address

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.algoliademo1.api.PostalPincodeApi
import com.example.algoliademo1.data.source.local.entity.Address
import com.example.algoliademo1.data.source.local.entity.Order
import com.example.algoliademo1.data.source.remote.FirebaseService
import com.example.algoliademo1.data.source.repository.AddressRepository
import com.example.algoliademo1.data.source.repository.CartRepository
import com.example.algoliademo1.data.source.repository.OrdersRepository
import com.example.algoliademo1.model.AddressModel
import com.example.algoliademo1.model.PincodeDetail
import com.example.algoliademo1.model.PincodeInfo
import com.example.algoliademo1.model.PincodeModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

class AddressViewModel : ViewModel() {

    private val addressRepository = AddressRepository.getRepository()
    private val ordersRepository = OrdersRepository.getRepository()
    private val cartRepository = CartRepository.getRepository()

    private val _address = MutableLiveData<String>()  // Not sure where used

    val address: LiveData<String>
        get() = _address

    private val _addresses = MutableLiveData<List<Address>>()

    val addresses: LiveData<List<Address>>
        get() = _addresses

    val pincodeModel = MutableLiveData<List<PincodeModel>?>()

    var addressId: String? = null

    var pincodeDetail: PincodeDetail? = null

    fun addAddress(doorNumber: String, address: String, city: String, pincode: Int, state: String) {
        viewModelScope.launch {
            val addressModel = AddressModel(address, city, doorNumber, pincode, state)

            val addressId = generateId()

            addressRepository.addAddress(FirebaseService.userId, addressId, addressModel)

            getAddresses()
        }
    }

    private fun generateId(): String {
        val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')

        return List(10) { charset.random() }
            .joinToString("")
    }

    fun getAddresses() {
        viewModelScope.launch {
            val addresses = addressRepository.getAddressList(FirebaseService.userId)

            _addresses.value = addresses
        }
    }

    fun getAddressList(addresses: List<Address>): List<String> {
        val addressList = mutableListOf<String>()

        for (address in addresses) {
            addressList.add(
                address.doorNumber + " " + address.address + " " + address.city +
                        " " + address.state + " " + address.pincode
            )
        }
        return addressList
    }

    suspend fun placeOrder(): Order {
        val orderId = generateId()
        val userId = FirebaseService.userId
        Log.d(TAG, "placeOrder: before async")
        val order = viewModelScope.async {
            var total = 0.0f
            Log.d(TAG, "placeOrder: inside vm scope")
            if (addressId != null) {
                Log.d(TAG, "placeOrder: $addressId")
                total = cartRepository.getCartTotal(userId)

                Log.d(TAG, "placeOrder: placing order")
                ordersRepository.placeOrder(
                    userId,
                    orderId,
                    addressId!!,
                    cartRepository.getCartItems(userId),
                    total
                )

                Log.d(TAG, "placeOrder: empty cart")
                cartRepository.emptyCart(userId)
            }

            Log.d(TAG, "placeOrder: ${Order(orderId, addressId!!, Date(), total)}")
            return@async Order(orderId, addressId!!, Date(), total)
        }
        Log.d(TAG, "placeOrder: before await")
        return order.await()
    }

    fun getPincodeDetails(pincode: String) {
        viewModelScope.launch {
            val details = PostalPincodeApi.service.getPincodeDetails(pincode)
            pincodeModel.value = details
        }
    }

    fun addPincodeDetail(postOffices: List<PincodeInfo>) {
        val cities = mutableListOf<String>()
        for(postOffice in postOffices)
            cities.add(postOffice.name + ", " + postOffice.district)

        pincodeDetail = PincodeDetail(
            cities,
            postOffices[0].state!!, postOffices[0].pincode!!.toInt()
        )
    }

}