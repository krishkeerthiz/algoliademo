package com.example.algoliademo1.model

class WishlistModel {
    var products : List<String>? = null

    constructor()

    constructor(productIds: List<String>){
        products = productIds
    }
}