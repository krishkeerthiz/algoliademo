package com.example.algoliademo1.model

import java.io.Serializable

data class ProductModel(
    var brand: String = "",
    var categories: List<String> = listOf(),
    var description: String = "",
    var free_shipping: Boolean = false,
    var image: String = "",
    var name: String = "",
    var objectId: String = "",
    var popularity: Int = 0,
    var price: Float = 0.0f,
    var price_range: String = "",
    var rating: Int = 0,
    var type: String = "",
    var url: String = "",
) : Serializable {
    override fun toString(): String {
        return "Name: $name\nPrice: $price\nBrand: $brand\nCategories: $categories\nType: $type\nDescription: $description\n" +
                "Rating: $rating\nFree Shipping: ${convertBoolean(free_shipping)}\nPrice Range: $price_range"
    }

    private fun convertBoolean(result: Boolean): String {
        return if (result) "Yes" else
            "No"
    }

}