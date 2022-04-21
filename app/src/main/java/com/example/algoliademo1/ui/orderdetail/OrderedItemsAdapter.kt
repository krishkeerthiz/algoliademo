package com.example.algoliademo1

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.algoliademo1.data.source.local.entity.Order
import com.example.algoliademo1.data.source.repository.OrdersRepository
import com.example.algoliademo1.data.source.repository.ProductsRepository
import com.example.algoliademo1.databinding.OrderItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderedItemsAdapter(val orderId: String, val onClickListener: OrderedItemOnClickListener) :
    ListAdapter<String, OrderedItemsViewHolder>(OrderedItemsAdapter) {

    private val ordersRepository = OrdersRepository.getRepository()
    private val productsRepository = ProductsRepository.getRepository()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderedItemsViewHolder {
        val view = LayoutInflater.from(parent.context)
        val binding = OrderItemBinding.inflate(view, parent, false)
        return OrderedItemsViewHolder(binding, productsRepository)
    }

    override fun onBindViewHolder(holder: OrderedItemsViewHolder, position: Int) {
        var productId = getItem(position)

        Log.d(TAG, "position $position product id: $productId")
        //       if(countValues.isNotEmpty())
        holder.bind(productId, orderId, ordersRepository)

        holder.binding.orderRatingText.setOnClickListener {
            onClickListener.onItemClick(productId)
        }
    }

    companion object : DiffUtil.ItemCallback<String>() {

        override fun areItemsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }
    }
}

class OrderedItemsViewHolder(
    val binding: OrderItemBinding,
    val productsRepository: ProductsRepository
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(productId: String, orderId: String, ordersRepository: OrdersRepository) {
        CoroutineScope(Dispatchers.Main).launch {
            val productModel = withContext(Dispatchers.IO) {
                productsRepository.getProduct(productId)
            }
            binding.orderItemName.text = productModel?.name

            binding.orderItemPrice.text =
                binding.orderItemPrice.context.getString(R.string.currency) + String.format(
                    "%.2f",
                    productModel.price
                )

            Glide.with(binding.orderItemImage.context)
                .load(productModel?.image)
                .placeholder(R.drawable.spinner1)
                .into(binding.orderItemImage)

            val productQuantity = withContext(Dispatchers.IO) {
                ordersRepository.getOrderItemQuantity(orderId, productId)
            }
            binding.orderItemCount.text = productQuantity.toString()

            binding.orderItemTotalPrice.text =
                binding.orderItemTotalPrice.context.getString(R.string.currency) + String.format(
                    "%.2f",
                    (productModel.price) * productQuantity
                )
        }
    }
}

class OrderedItemOnClickListener(
    val itemClickListener: (productId: String) -> Unit
) {
    fun onItemClick(productId: String) = itemClickListener(productId)
}