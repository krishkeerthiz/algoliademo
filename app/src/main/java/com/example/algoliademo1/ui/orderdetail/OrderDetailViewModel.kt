package com.example.algoliademo1.ui.orderdetail

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.algoliademo1.data.source.local.entity.Product
import com.example.algoliademo1.data.source.remote.FirebaseService
import com.example.algoliademo1.data.source.repository.AddressRepository
import com.example.algoliademo1.data.source.repository.OrdersRepository
import com.example.algoliademo1.data.source.repository.ProductsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderDetailViewModel : ViewModel() {
    private val ordersRepository = OrdersRepository.getRepository()
    private val addressRepository = AddressRepository.getRepository()
    private val productsRepository = ProductsRepository.getRepository()

    lateinit var orderId: String

    val addressFlag = MutableLiveData<Boolean>()
    private val _address = MutableLiveData<String>()

    val address: LiveData<String>
        get() = _address


    val ordersFlag = MutableLiveData<Boolean>()

    private val _orders = MutableLiveData<List<String>>()

    val orders: LiveData<List<String>>
        get() = _orders

    init {
        addressFlag.value = false
        ordersFlag.value = false
    }

    fun getAddress() {
        viewModelScope.launch {
            val addressId = ordersRepository.getOrder(orderId).addressId

            _address.value =
                addressRepository.getAddress(addressId, FirebaseService.userId).toString()
            addressFlag.value = true
        }

    }

    fun getOrderItems() {
        Log.d("ContentValues", "Inside ordered items")
        viewModelScope.launch {
            val orders = ordersRepository.getOrderItemsId(orderId)
            _orders.value = orders.distinct()

            ordersFlag.value = true
        }
    }

    suspend fun getOrderItemQuantity(orderId: String, productId: String) = withContext(Dispatchers.IO) {
        ordersRepository.getOrderItemQuantity(orderId, productId)
    }

    suspend fun getOrderProducts(productIds: List<String>?): List<Product> {
        var products = listOf<Product>()
        viewModelScope.launch {
            products = productsRepository.getProducts(productIds)
        }.join()
        Log.d(TAG, "getCartProducts: ${products.size}")
        return products
    }

    fun addRating(productId: String, rating: Int){
        viewModelScope.launch {
            productsRepository.addRating(productId, rating)
        }
    }
}

