package com.example.algoliademo1.model

//class CartModel(products: Map<String, Int>, var total: Float) {
//    var products: Map<String, Int>? = products
//}

data class CartModel(
    var products: Map<String, Int>?,
    var total: Float
    )