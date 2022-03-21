package com.example.algoliademo1

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.algoliademo1.databinding.CartItemBinding
import com.example.algoliademo1.model.CartModel
import com.example.algoliademo1.model.ProductModel
import com.example.algoliademo1.data.source.remote.FirebaseService
import com.google.firebase.firestore.ktx.toObject

class CartAdapter(val onClickListener: CartOnClickListener) : ListAdapter<String, CartViewHolder>(CartAdapter) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
        val binding = CartItemBinding.inflate(view, parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val productId = getItem(position)
        holder.bind(productId)

        holder.binding.deleteImage.setOnClickListener {
            val price = holder.binding.cartItemPrice.text.toString().trimStart('₹').toFloat()
            onClickListener.deleteClickListener(productId, price)
        }

        holder.itemView.setOnClickListener {
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

class CartViewHolder(val binding: CartItemBinding) : RecyclerView.ViewHolder(binding.root){
    fun bind(productId: String) {
//        binding.productName.text = productModel.name
//        binding.productBrand.text = productModel.brand
        FirebaseService.testGetProductReference(productId).get().addOnSuccessListener {
            val productModel = it.toObject<ProductModel>()

            binding.cartItemName.text = productModel?.name

            binding.cartItemPrice.text = "₹" + productModel?.price

            Glide.with(binding.cartItemImage.context)
                .load(productModel?.image)
                .into(binding.cartItemImage)
        }

        FirebaseService.getCartReference().get().addOnSuccessListener {
            val cartModel = it.toObject<CartModel>()

            binding.cartItemCount.text = cartModel?.products?.get(productId).toString() + " n."
        }
    }
}

class CartOnClickListener(
    val itemClickListener : (productId: String) -> Unit,
    val deleteClickListener : (productId: String, price: Float) ->Unit
){

    fun onItemClick(productId: String) = itemClickListener(productId)

    fun onDeleteClick(productId: String, price: Float) = deleteClickListener(productId, price)
}