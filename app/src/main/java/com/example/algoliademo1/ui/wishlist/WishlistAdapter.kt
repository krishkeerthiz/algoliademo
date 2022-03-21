package com.example.algoliademo1

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.algoliademo1.databinding.WishlistItemBinding
import com.example.algoliademo1.model.ProductModel
import com.example.algoliademo1.data.source.remote.FirebaseService
import com.google.firebase.firestore.ktx.toObject


class WishlistAdapter(val onClickListener: WishlistClickListener) : ListAdapter<String, WishlistViewHolder>(WishlistAdapter) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishlistViewHolder {
        val view = LayoutInflater.from(parent.context)
        val binding = WishlistItemBinding.inflate(view, parent, false)
        return WishlistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WishlistViewHolder, position: Int) {
        val productId = getItem(position)
        holder.bind(productId)

        holder.binding.addToCartButton.setOnClickListener {
            val price = holder.binding.wishlistItemPrice.text.trimStart('₹').toString().toFloat()
            onClickListener.onAddItemClick(productId, price)
            Toast.makeText(it.context, "Added to cart", Toast.LENGTH_SHORT).show()
        }

        holder.binding.removeButton.setOnClickListener {
            onClickListener.onRemoveItemClick(productId)
            Toast.makeText(it.context, "removed from wishlist", Toast.LENGTH_SHORT).show()
        }

        holder.itemView.setOnClickListener {
            onClickListener.onItemClick(productId)
            Toast.makeText(it.context, "Item clicked", Toast.LENGTH_SHORT).show()
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

class WishlistViewHolder(val binding: WishlistItemBinding) : RecyclerView.ViewHolder(binding.root){

    fun bind(productId: String){
        FirebaseService.testGetProductReference(productId).get().addOnSuccessListener {
            val productModel = it.toObject<ProductModel>()

            binding.wishlistItemName.text = productModel?.name

            binding.wishlistItemPrice.text = "₹" + productModel?.price

            Glide.with(binding.cartItemImage.context)
                .load(productModel?.image)
                .into(binding.cartItemImage)
        }
    }
}

class WishlistClickListener(val itemClickListener: (productId: String) -> Unit,
val addItemClickListener: (productId: String, price: Float) -> Unit,
val removeItemClickListener: (productId: String) ->Unit){

    fun onItemClick(productId: String) = itemClickListener(productId)

    fun onAddItemClick(productId: String, price: Float) = addItemClickListener(productId, price)

    fun onRemoveItemClick(productId: String) = removeItemClickListener(productId)
}