package com.example.algoliademo1.ui.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.algoliademo1.R
import com.example.algoliademo1.databinding.CartItemBinding
import com.example.algoliademo1.model.ProductQuantityModel

class CartAdapter(private val onClickListener: CartOnClickListener) :
    RecyclerView.Adapter<CartViewHolder>() {

    // Products and quantities
    private var productQuantityModels: List<ProductQuantityModel> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {

        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return CartViewHolder(binding).apply {

            binding.cartItemName.setOnClickListener {
                val productQuantity = productQuantityModels[absoluteAdapterPosition]
                onClickListener.onItemClick(productQuantity.product.productId)
            }

            binding.cartItemImage.setOnClickListener {
                val productQuantity = productQuantityModels[absoluteAdapterPosition]
                onClickListener.onItemClick(productQuantity.product.productId)
            }

            binding.cartItemPrice.setOnClickListener {
                val productQuantity = productQuantityModels[absoluteAdapterPosition]
                onClickListener.onItemClick(productQuantity.product.productId)
            }

            // Add button click listener
            binding.addButton.setOnClickListener {
                val productQuantity = productQuantityModels[absoluteAdapterPosition]
                onClickListener.onIncrementClick(productQuantity.product.productId)
            }

            // Remove button click listener
            binding.removeButton.setOnClickListener {
                val productQuantity = productQuantityModels[absoluteAdapterPosition]
                onClickListener.onDecrementClick(productQuantity.product.productId)
            }

            // Delete button click listener
            binding.deleteImage.setOnClickListener {
                val productQuantity = productQuantityModels[absoluteAdapterPosition]
                val priceText = binding.cartItemPrice.text.toString()
                var price = 0.0f
                if (priceText != "")
                    price = priceText.trimStart('$').toFloat()

                onClickListener.onDeleteClick(productQuantity.product.productId, price)
            }

        }

    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val productQuantity = productQuantityModels[position]

        holder.bind(productQuantity)

//        holder.binding.apply {
//            // Restricting click to product detail only on item name, item image, item price
//
//            cartItemName.setOnClickListener {
//                onClickListener.onItemClick(productQuantity.product.productId)
//            }
//
//            cartItemImage.setOnClickListener {
//                onClickListener.onItemClick(productQuantity.product.productId)
//            }
//
//            cartItemPrice.setOnClickListener {
//                onClickListener.onItemClick(productQuantity.product.productId)
//            }
//
//            // Add button click listener
//            addButton.setOnClickListener {
//                onClickListener.onIncrementClick(productQuantity.product.productId)
//            }
//
//            // Remove button click listener
//            removeButton.setOnClickListener {
//                onClickListener.onDecrementClick(productQuantity.product.productId)
//            }
//
//            // Delete button click listener
//            deleteImage.setOnClickListener {
//                val priceText = holder.binding.cartItemPrice.text.toString()
//                var price = 0.0f
//                if (priceText != "")
//                    price = priceText.trimStart('$').toFloat()
//
//                onClickListener.onDeleteClick(productQuantity.product.productId, price)
//            }
//        }
    }

    override fun getItemCount() = productQuantityModels.size

    fun addProductQuantities(models: List<ProductQuantityModel>) {
        productQuantityModels = models
        notifyDataSetChanged()
    }

}

class CartViewHolder(val binding: CartItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(productQuantity: ProductQuantityModel) {
        val product = productQuantity.product
        val productCount = productQuantity.quantity

        binding.cartItemName.text = product.name

        val cartItemPrice =
            binding.cartItemPrice.context.getString(R.string.currency) + String.format(
                "%.2f",
                product.price
            )

        binding.cartItemPrice.text = cartItemPrice

        Glide.with(binding.cartItemImage.context)
            .load(product.image)
            .placeholder(R.drawable.spinner1)
            .into(binding.cartItemImage)


        binding.removeButton.visibility = if (productCount == 1) View.INVISIBLE else View.VISIBLE

        binding.addButton.visibility = if (productCount == 5) View.INVISIBLE else View.VISIBLE

        binding.count.text = productCount.toString()

    }
}

class CartOnClickListener(
    private val itemClickListener: (productId: String) -> Unit,
    private val deleteClickListener: (productId: String, price: Float) -> Unit,
    private val incrementClickListener: (productId: String) -> Unit,
    private val decrementClickListener: (productId: String) -> Unit
) {

    fun onItemClick(productId: String) = itemClickListener(productId)

    fun onDeleteClick(productId: String, price: Float) = deleteClickListener(productId, price)

    fun onIncrementClick(productId: String) = incrementClickListener(productId)

    fun onDecrementClick(productId: String) = decrementClickListener(productId)
}