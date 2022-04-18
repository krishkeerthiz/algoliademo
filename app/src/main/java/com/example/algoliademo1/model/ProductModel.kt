package com.example.algoliademo1.model

class ProductModel {
    var brand: String = ""
    var categories: MutableList<String> = mutableListOf()
    var description: String = ""
    var free_shipping: Boolean = false
    var image: String = ""
    var name: String = ""
    var objectId: String = ""
    var popularity: Int = 0
    var price: Float = 0.0f
    var price_range: String = ""
    var rating: Int = 0
    var type: String = ""
    var url: String = ""

    constructor()
}
