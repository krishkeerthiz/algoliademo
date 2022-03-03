package com.example.algoliademo1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.algoliademo1.databinding.ProductItemBinding

class ProductAdapter : PagedListAdapter<Product, ProductViewHolder>(ProductAdapter) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
        val binding = ProductItemBinding.inflate(view, parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)

        if (product != null) holder.bind(product)
    }

    companion object : DiffUtil.ItemCallback<Product>() {

        override fun areItemsTheSame(
            oldItem: Product,
            newItem: Product
        ): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(
            oldItem: Product,
            newItem: Product
        ): Boolean {
            return oldItem == newItem
        }
    }
}

class ProductViewHolder(val binding: ProductItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(product: Product) {
        binding.productName.text = product.name
    }
}
