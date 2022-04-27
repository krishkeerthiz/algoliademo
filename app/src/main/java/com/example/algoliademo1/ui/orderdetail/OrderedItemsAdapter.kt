package com.example.algoliademo1.ui.orderdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.algoliademo1.R
import com.example.algoliademo1.databinding.OrderItemBinding
import com.example.algoliademo1.model.ProductQuantityModel

class OrderedItemsAdapter(val orderId: String, val onClickListener: OrderedItemOnClickListener) :
    RecyclerView.Adapter<OrderedItemsViewHolder>() {

    private var productQuantityModels: List<ProductQuantityModel> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderedItemsViewHolder {
        val view = LayoutInflater.from(parent.context)
        val binding = OrderItemBinding.inflate(view, parent, false)
        return OrderedItemsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderedItemsViewHolder, position: Int) {
        val productQuantity = productQuantityModels[position]

        holder.bind(productQuantity)

        holder.binding.orderRatingText.setOnClickListener {
            onClickListener.onItemClick(productQuantity.product.productId)
        }
    }

    override fun getItemCount() = productQuantityModels.size

    fun addProductQuantityModels(models: List<ProductQuantityModel>){
        productQuantityModels = models
        notifyDataSetChanged()
    }

}

class OrderedItemsViewHolder(
    val binding: OrderItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(productQuantity: ProductQuantityModel) {
        val product = productQuantity.product
        val productCount = productQuantity.quantity

        binding.orderItemName.text = product.name

        val price = binding.orderItemPrice.context.getString(R.string.currency) + String.format("%.2f", product.price)

        binding.orderItemPrice.text = price

        Glide.with(binding.orderItemImage.context)
            .load(product.image)
            .placeholder(R.drawable.spinner1)
            .into(binding.orderItemImage)

        binding.orderItemCount.text = productCount.toString()

        val totalPrice = binding.orderItemTotalPrice.context.getString(R.string.currency) + String.format("%.2f", (product.price) * productCount)

        binding.orderItemTotalPrice.text = totalPrice

    }
}

class OrderedItemOnClickListener(
    val itemClickListener: (productId: String) -> Unit) {
    fun onItemClick(productId: String) = itemClickListener(productId)
}