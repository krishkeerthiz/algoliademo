package com.example.algoliademo1.ui.orders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.algoliademo1.R
import com.example.algoliademo1.data.source.local.entity.Order
import com.example.algoliademo1.databinding.OrderCardBinding
import com.example.algoliademo1.model.OrderAddressModel
import java.text.SimpleDateFormat
import java.util.*

class OrdersAdapter(val onClickListener: OrdersOnClickListener) :
    ListAdapter<OrderAddressModel, OrdersViewHolder>(OrdersAdapter) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        val view = LayoutInflater.from(parent.context)
        val binding = OrderCardBinding.inflate(view, parent, false)
        return OrdersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        val orderAddress = currentList[position]
        holder.bind(orderAddress)

        holder.itemView.setOnClickListener {
            onClickListener.onItemClick(orderAddress.order)
        }
    }

    companion object : DiffUtil.ItemCallback<OrderAddressModel>() {

        override fun areItemsTheSame(
            oldItem: OrderAddressModel,
            newItem: OrderAddressModel
        ): Boolean {
            return oldItem.order.orderId == newItem.order.orderId
        }

        override fun areContentsTheSame(
            oldItem: OrderAddressModel,
            newItem: OrderAddressModel
        ): Boolean {
            return oldItem == newItem
        }

    }
}

class OrdersViewHolder(val binding: OrderCardBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(orderAddress: OrderAddressModel) {

        binding.orderAddress.text = orderAddress.address
        binding.orderDate.text = formatDate(orderAddress.order.date)

        val totalPrice =
            binding.orderTotalPrice.context.getString(R.string.currency) + String.format(
                "%.2f",
                orderAddress.order.total
            )
        binding.orderTotalPrice.text = totalPrice

    }

    private fun formatDate(date: Date): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.UK)
        return sdf.format(date).toString()
    }

}

class OrdersOnClickListener(
    val itemClickListener: (order: Order) -> Unit
) {
    fun onItemClick(order: Order) = itemClickListener(order)
}