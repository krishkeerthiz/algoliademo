package com.example.algoliademo1

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.algoliademo1.databinding.ProductCardBinding
import com.example.algoliademo1.model.ProductInfo
import com.example.algoliademo1.model.ProductModel
import com.example.algoliademo1.data.source.remote.FirebaseService
import com.google.firebase.firestore.ktx.toObject

class ProductAdapter(
    val onClickListener: OnClickListener
) : ListAdapter<ProductInfo, ProductViewHolder>(ProductAdapter) {  // PagedListAdapter is converted to ListAdapter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
        val binding = ProductCardBinding.inflate(view, parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        if(product != null){
            holder.bind(product)
//            val productModel = FirebaseService.getProduct(product.id)
//            if (productModel != null) holder.bind(productModel)
        }

        holder.itemView.setOnClickListener {
            onClickListener.onClick(product!!)
        }
    }

    companion object : DiffUtil.ItemCallback<ProductInfo>() {

        override fun areItemsTheSame(
            oldItem: ProductInfo,
            newItem: ProductInfo
        ): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(
            oldItem: ProductInfo,
            newItem: ProductInfo
        ): Boolean {
            return oldItem == newItem
        }
    }

}

class ProductViewHolder(val binding: ProductCardBinding) : RecyclerView.ViewHolder(binding.root){

    fun bind(productInfo: ProductInfo) {
//        binding.productName.text = productModel.name
//        binding.productBrand.text = productModel.brand
        FirebaseService.testGetProductReference(productInfo.id).get().addOnSuccessListener {
            val productModel = it.toObject<ProductModel>()

            binding.productName.text = productModel?.name
            binding.productBrand.text = productModel?.brand

            binding.productPrice.text = "₹" + productModel?.price

            binding.productRating.rating = productModel?.rating?.div(2)?.toFloat() ?: 2.5f

            Glide.with(binding.productImage.context)
                .load(productModel?.image)
                .into(binding.productImage)
        }
    }
}

class OnClickListener(val clickListener: (productInfo: ProductInfo) -> Unit){
    fun onClick(productInfo: ProductInfo) = clickListener(productInfo)
}


