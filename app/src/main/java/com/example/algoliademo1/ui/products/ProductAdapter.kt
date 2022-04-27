package com.example.algoliademo1.ui.products

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.algoliademo1.R
import com.example.algoliademo1.data.source.local.entity.Product
import com.example.algoliademo1.databinding.ProductCardBinding

class ProductAdapter(
    val onClickListener: OnClickListener
) : RecyclerView.Adapter<ProductViewHolder>() {

    private var products: List<Product?> = listOf()

    private val limit = 50

    fun addProducts(products: List<Product?>){
        Log.d(TAG, "addProducts: product adapter")
        this.products = products
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
        val binding = ProductCardBinding.inflate(view, parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        //holder.setIsRecyclable(false) // to prevent showing old data while loading new data
        val product = products[position]

        if(product != null)  // First time product could become null
            holder.bind(product)


        holder.itemView.setOnClickListener {
            onClickListener.onClick(product!!.productId)
        }
    }

    override fun getItemCount(): Int {
        return if(products.size > limit) limit else products.size
        //return products.size
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


