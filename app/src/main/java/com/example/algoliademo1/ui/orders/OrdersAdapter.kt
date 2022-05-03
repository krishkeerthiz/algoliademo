package com.example.algoliademo1.ui.orders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.algoliademo1.R
import com.example.algoliademo1.data.source.local.entity.Order
import com.example.algoliademo1.databinding.OrderCardBinding
import com.example.algoliademo1.model.OrderAddressModel
import com.example.algoliademo1.util.formatDate

class OrdersAdapter(private val onClickListener: OrdersOnClickListener) :
    RecyclerView.Adapter<OrdersViewHolder>() {

    private var orderAddresses: List<OrderAddressModel> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        val view = LayoutInflater.from(parent.context)
        val binding = OrderCardBinding.inflate(view, parent, false)

        val viewHolder = OrdersViewHolder(binding).apply {
            itemView .setOnClickListener {
                onClickListener.onItemClick(orderAddresses[absoluteAdapterPosition].order)
            }
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        val orderAddress = orderAddresses[position]
        holder.bind(orderAddress)

//        holder.itemView.setOnClickListener {
//            onClickListener.onItemClick(orderAddress.order)
//        }

    }

    override fun getItemCount() = orderAddresses.size

    fun addOrderAddresses(models: List<OrderAddressModel>){
        orderAddresses = models
        notifyDataSetChanged()
       // OrderCardBinding.inflate()
    }
}

class OrdersViewHolder(val binding: OrderCardBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(orderAddress: OrderAddressModel) {

        binding.orderAddress.text = orderAddress.address
        binding.orderDate.text = formatDate(orderAddress.order.date)

        val totalPrice = binding.orderTotalPrice.context.getString(R.string.currency) + String.format("%.2f", orderAddress.order.total)
        binding.orderTotalPrice.text = totalPrice

    }
}

class OrdersOnClickListener(
    private val itemClickListener: (order: Order) -> Unit
) {
    fun onItemClick(order: Order) = itemClickListener(order)
}