package com.example.algoliademo1.model

class CartModel {
    var products: Map<String, Int>? = null
    var total: Float = 0.0f

    constructor()

    constructor(products: Map<String, Int>, total: Float) {
        this.products = products
        this.total = total
    }
}
