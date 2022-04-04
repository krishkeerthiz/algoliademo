package com.example.algoliademo1

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.algoliademo1.data.source.local.entity.Order
import com.example.algoliademo1.data.source.remote.FirebaseService
import com.example.algoliademo1.data.source.repository.AddressRepository
import com.example.algoliademo1.databinding.OrderCardBinding
import com.example.algoliademo1.model.AddressModel
import com.example.algoliademo1.model.CartModel
import com.example.algoliademo1.model.OrderModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.*
import okhttp3.internal.addHeaderLenient
import okhttp3.internal.format
import java.text.SimpleDateFormat
import java.util.*

class OrdersAdapter(val onClickListener: OrdersOnClickListener)
    : ListAdapter<Order, OrdersViewHolder>(OrdersAdapter) {

    private val addressRepository = AddressRepository.getRepository()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        val view = LayoutInflater.from(parent.context)
        val binding = OrderCardBinding.inflate(view, parent, false)
        return OrdersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        val order = getItem(position)
        holder.bind(order, addressRepository)

        holder.itemView.setOnClickListener {
            onClickListener.onItemClick(order)
        }
    }

    companion object : DiffUtil.ItemCallback<Order>() {

        override fun areItemsTheSame(
            oldItem: Order,
            newItem: Order
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: Order,
            newItem: Order
        ): Boolean {
            return oldItem == newItem
        }

    }
}

class OrdersViewHolder(val binding: OrderCardBinding) : RecyclerView.ViewHolder(binding.root){

    val scope = CoroutineScope(Dispatchers.IO)

    fun bind(order: Order, addressRepository: AddressRepository){
       // CoroutineScope(Dispatchers.Main).launch {
        scope.launch {
            val address = addressRepository.getAddress(order.addressId, FirebaseService.userId).toString()

            withContext(Dispatchers.Main){
                binding.orderAddress.text = address
                binding.orderDate.text = formatDate(order.date)
                binding.orderTotalPrice.text = order.total.toString()
            }
        }

//            binding.orderAddress.text = CoroutineScope(Dispatchers.IO).async {
//                addressRepository.getAddress(order.addressId, FirebaseService.userId).toString()
//            }

//            binding.orderDate.text = formatDate(order.date)
//            binding.orderTotalPrice.text = order.total.toString()
        //}
    }

    fun formatDate(date: Date): String{
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        return sdf.format(date).toString()
    }

    fun getDate(timeStamp: Timestamp): String{
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val milliSeconds = timeStamp.seconds * 1000 + timeStamp.nanoseconds/1000000
        val netDate = Date(milliSeconds)
        return sdf.format(netDate).toString()
    }

}

class OrdersOnClickListener(
    val itemClickListener : (order: Order) -> Unit){

    fun onItemClick(order: Order) = itemClickListener(order)
}