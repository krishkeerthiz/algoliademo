package com.example.algoliademo1.ui.cart

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.algoliademo1.R
import com.example.algoliademo1.databinding.CartItemBinding
import com.example.algoliademo1.model.ProductQuantityModel

class CartAdapter(val onClickListener: CartOnClickListener) :
    ListAdapter<ProductQuantityModel, CartViewHolder>(CartAdapter) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        return CartViewHolder(CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: called")
        val productQuantity = currentList[position]

        holder.bind(productQuantity)

        holder.binding.deleteImage.setOnClickListener {
            val priceText = holder.binding.cartItemPrice.text.toString()
            var price = 0.0f
            if (priceText != "")
                price = priceText.trimStart('$').toFloat()
            //bug occurs here sometimes
            onClickListener.onDeleteClick(productQuantity.product.productId, price)
        }

        holder.binding.cartItemName.setOnClickListener {
            onClickListener.onItemClick(productQuantity.product.productId)
        }

        holder.binding.cartItemImage.setOnClickListener {
            onClickListener.onItemClick(productQuantity.product.productId)
        }
        holder.binding.cartItemPrice.setOnClickListener {
            onClickListener.onItemClick(productQuantity.product.productId)
        }

        holder.binding.addButton.setOnClickListener {
            onClickListener.onIncrementClick(productQuantity.product.productId)
        }

        holder.binding.removeButton.setOnClickListener {
            onClickListener.onDecrementClick(productQuantity.product.productId)
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

class CartViewHolder(
    val binding: CartItemBinding
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(productQuantity: ProductQuantityModel) {
        val product = productQuantity.product
        val productCount = productQuantity.quantity

        binding.cartItemName.text = product.name

        val cartItemPrice = binding.cartItemPrice.context.getString(R.string.currency) + String.format(
            "%.2f",
            product.price
        )

        binding.cartItemPrice.text = cartItemPrice

        Glide.with(binding.cartItemImage.context)
            .load(product.image)
            .placeholder(R.drawable.spinner1)
            .into(binding.cartItemImage)

        if (productCount == 1)
            binding.removeButton.visibility = View.INVISIBLE
        else
            binding.removeButton.visibility = View.VISIBLE

        if (productCount == 5)
            binding.addButton.visibility = View.INVISIBLE
        else
            binding.addButton.visibility = View.VISIBLE

        binding.count.text = productCount.toString()

    }
}

class CartOnClickListener(
    val itemClickListener: (productId: String) -> Unit,
    val deleteClickListener: (productId: String, price: Float) -> Unit,
    val incrementClickListener: (productId: String) -> Unit,
    val decrementClickListener: (productId: String) -> Unit
) {

    fun onItemClick(productId: String) = itemClickListener(productId)

    fun onDeleteClick(productId: String, price: Float) = deleteClickListener(productId, price)

    fun onIncrementClick(productId: String) = incrementClickListener(productId)

    fun onDecrementClick(productId: String) = decrementClickListener(productId)
}