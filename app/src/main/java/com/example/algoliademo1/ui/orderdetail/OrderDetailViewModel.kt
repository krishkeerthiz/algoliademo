package com.example.algoliademo1.ui.orderdetail

import android.content.Context
import androidx.lifecycle.*
import com.example.algoliademo1.data.source.local.entity.Product
import com.example.algoliademo1.data.source.remote.FirebaseService
import com.example.algoliademo1.data.source.repository.AddressRepository
import com.example.algoliademo1.data.source.repository.OrdersRepository
import com.example.algoliademo1.data.source.repository.ProductsRepository
import com.example.algoliademo1.model.ProductQuantityModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderDetailViewModel(context: Context) : ViewModel() {
    // Repositories
    private val ordersRepository = OrdersRepository.getRepository(context)
    private val addressRepository = AddressRepository.getRepository(context)
    private val productsRepository = ProductsRepository.getRepository(context)

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
        ordersRepository.getOrderItemQuantity(orderId, productId)


    private suspend fun getOrderProducts(productIds: List<String>?): List<Product?> =
         productsRepository.getProducts(productIds)


    fun addRating(productId: String, rating: Int) {
        viewModelScope.launch {
            productsRepository.addRating(productId, rating)
        }
    }

    suspend fun getProductsQuantity(
        productIds: List<String>,
        orderId: String
    ): List<ProductQuantityModel> {

        return withContext(Dispatchers.Default){
            val productsQuantity = mutableListOf<ProductQuantityModel>()

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

            productsQuantity
        }
    }

    suspend fun getProduct(productId: String) =
        productsRepository.getProduct(productId)

    suspend fun getUserRating(productId: String) =
        productsRepository.getUserRating(productId)
}


class OrderDetailViewModelFactory(private val context: Context) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OrderDetailViewModel(context) as T
    }
}

