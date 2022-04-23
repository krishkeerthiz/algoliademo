package com.example.algoliademo1.ui.products

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.algoliademo1.R
import com.example.algoliademo1.data.source.local.entity.Product
import com.example.algoliademo1.databinding.ProductCardBinding

class ProductAdapter(
    val onClickListener: OnClickListener
) : ListAdapter<Product, ProductViewHolder>(ProductAdapter) {  // diffutils can also be passed as parameter to list adapter, when diffutils
    // is created as a separate class.

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
        val binding = ProductCardBinding.inflate(view, parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {

        val product = currentList[position]
        if (product != null) {
            holder.bind(product)
        }

        holder.itemView.setOnClickListener {
            onClickListener.onClick(product!!.productId)
        }
    }

    companion object : DiffUtil.ItemCallback<Product>() {

        override fun areItemsTheSame(
            oldItem: Product,
            newItem: Product
        ): Boolean {
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(
            oldItem: Product,
            newItem: Product
        ): Boolean {
            return oldItem == newItem
        }
    }

}

class ProductViewHolder(
    val binding: ProductCardBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(product: Product) {

        binding.apply {
            productName.text = product.name
            productBrand.text = product.brand

            val price = "$${product.price}"
            productPrice.text = price

            productRating.rating = product.rating.div(2).toFloat()

            Glide.with(productImage.context)
                .load(product.image)
                .placeholder(R.drawable.spinner1)
                .into(binding.productImage)
        }

    }
}

class OnClickListener(
    val clickListener: (productId: String) -> Unit
) {
    fun onClick(productId: String) = clickListener(productId)
}


