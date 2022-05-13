package com.example.algoliademo1.ui.orderdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.algoliademo1.data.source.local.entity.Product
import com.example.algoliademo1.data.source.remote.FirebaseService
import com.example.algoliademo1.data.source.repository.AddressRepository
import com.example.algoliademo1.data.source.repository.OrdersRepository
import com.example.algoliademo1.data.source.repository.ProductsRepository
import com.example.algoliademo1.model.ProductQuantityModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderDetailViewModel : ViewModel() {
    // Repositories
    private val ordersRepository = OrdersRepository //.getRepository()
    private val addressRepository = AddressRepository //.getRepository()
    private val productsRepository = ProductsRepository //.getRepository()

    lateinit var orderId: String

    // Live data
    val addressFlag = MutableLiveData<Boolean>()

    val ordersFlag = MutableLiveData<Boolean>()

    private val _address = MutableLiveData<String>()
    val address: LiveData<String>
        get() = _address

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
        viewModelScope.launch {
            val orders = ordersRepository.getOrderItemsId(orderId)
            _orders.value = orders.distinct()

            ordersFlag.value = true
        }
    }

    private suspend fun getOrderItemQuantity(orderId: String, productId: String) =
        withContext(Dispatchers.IO) {
            ordersRepository.getOrderItemQuantity(orderId, productId)
        }

    private suspend fun getOrderProducts(productIds: List<String>?): List<Product?> {
        var products = listOf<Product?>()
        viewModelScope.launch {
            products = productsRepository.getProducts(productIds)
        }.join()

        return products
    }

    fun addRating(productId: String, rating: Int) {
        viewModelScope.launch {
            productsRepository.addRating(productId, rating)
        }
    }

    suspend fun getProductsQuantity(
        productIds: List<String>,
        orderId: String
    ): List<ProductQuantityModel> {

        val productsQuantity = mutableListOf<ProductQuantityModel>()

        viewModelScope.launch {
            val products = getOrderProducts(productIds)

            for (product in products) {
                if(product != null)
                productsQuantity.add(
                    ProductQuantityModel(
                        product,
                        getOrderItemQuantity(orderId, product.productId)
                    )
                )
            }
        }.join()

        return productsQuantity
    }

    suspend fun getProduct(productId: String) = withContext(Dispatchers.IO) {
        productsRepository.getProduct(productId)
    }


    suspend fun getUserRating(productId: String) = withContext(Dispatchers.IO) {
        productsRepository.getUserRating(productId)
    }

}

