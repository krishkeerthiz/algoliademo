package com.example.algoliademo1.ui.wishlist

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.algoliademo1.R
import com.example.algoliademo1.data.source.local.entity.Product
import com.example.algoliademo1.databinding.WishlistItemBinding

class WishlistAdapter(private val onClickListener: WishlistClickListener) :
    RecyclerView.Adapter<WishlistViewHolder>() {

    private var wishlistProducts: List<Product?> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishlistViewHolder {
        val view = LayoutInflater.from(parent.context)
        val binding = WishlistItemBinding.inflate(view, parent, false)
        return WishlistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WishlistViewHolder, position: Int) {
        val product = wishlistProducts[position]

        if(product != null)
        holder.bind(product)

        holder.binding.addToCartButton.setOnClickListener {
            val price = holder.binding.wishlistItemPrice.text.trimStart('$').toString().toFloat()
            onClickListener.onAddItemClick(product?.productId, price)
            Toast.makeText(it.context, "Added to cart", Toast.LENGTH_SHORT).show()
        }

        holder.binding.removeButton.setOnClickListener {
            onClickListener.onRemoveItemClick(product?.productId)
        }

        holder.itemView.setOnClickListener {
            onClickListener.onItemClick(product?.productId)
        }
    }

    override fun getItemCount() = wishlistProducts.size

    fun addWishlistProducts(products: List<Product?>) {
        wishlistProducts = products
        notifyDataSetChanged()
    }

}

class WishlistViewHolder(
    val binding: WishlistItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(product: Product) {

        binding.wishlistItemName.text = product.name

        val price = binding.wishlistItemPrice.context.getString(R.string.currency) + product.price
        binding.wishlistItemPrice.text = price

        Glide.with(binding.cartItemImage.context)
            .load(product.image)
            .placeholder(R.drawable.spinner1)
            .into(binding.cartItemImage)
    }
}

class WishlistClickListener(
    private val itemClickListener: (productId: String?) -> Unit,
    private val addItemClickListener: (productId: String?, price: Float) -> Unit,
    private val removeItemClickListener: (productId: String?) -> Unit
) {

    fun onItemClick(productId: String?) = itemClickListener(productId)

    fun onAddItemClick(productId: String?, price: Float) = addItemClickListener(productId, price)

    fun onRemoveItemClick(productId: String?) = removeItemClickListener(productId)
}