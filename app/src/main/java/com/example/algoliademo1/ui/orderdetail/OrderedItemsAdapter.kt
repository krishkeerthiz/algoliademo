package com.example.algoliademo1.ui.orderdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.algoliademo1.R
import com.example.algoliademo1.databinding.OrderItemBinding
import com.example.algoliademo1.model.ProductQuantityModel

class OrderedItemsAdapter(val orderId: String, val onClickListener: OrderedItemOnClickListener) :
    ListAdapter<ProductQuantityModel, OrderedItemsViewHolder>(OrderedItemsAdapter) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderedItemsViewHolder {
        val view = LayoutInflater.from(parent.context)
        val binding = OrderItemBinding.inflate(view, parent, false)
        return OrderedItemsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderedItemsViewHolder, position: Int) {
        val productQuantity = currentList[position]

        holder.bind(productQuantity)

        holder.binding.orderRatingText.setOnClickListener {
            onClickListener.onItemClick(productQuantity.product.productId)
        }
    }

    companion object : DiffUtil.ItemCallback<ProductQuantityModel>() {

        override fun areItemsTheSame(
            oldItem: ProductQuantityModel,
            newItem: ProductQuantityModel
        ): Boolean {
            return oldItem.product.productId == newItem.product.productId
        }

        override fun areContentsTheSame(
            oldItem: ProductQuantityModel,
            newItem: ProductQuantityModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}

class OrderedItemsViewHolder(
    val binding: OrderItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(productQuantity: ProductQuantityModel) {
        val product = productQuantity.product
        val productCount = productQuantity.quantity

        binding.orderItemName.text = product.name

        val price = binding.orderItemPrice.context.getString(R.string.currency) + String.format(
            "%.2f",
            product.price
        )
        binding.orderItemPrice.text = price


        Glide.with(binding.orderItemImage.context)
            .load(product.image)
            .placeholder(R.drawable.spinner1)
            .into(binding.orderItemImage)

        binding.orderItemCount.text = productCount.toString()

        val totalPrice = binding.orderItemTotalPrice.context.getString(R.string.currency) + String.format(
            "%.2f",
            (product.price) * productCount
        )

        binding.orderItemTotalPrice.text = totalPrice


    }
}

class OrderedItemOnClickListener(
    val itemClickListener: (productId: String) -> Unit
) {
    fun onItemClick(productId: String) = itemClickListener(productId)
}