package com.example.algoliademo1.model

class AddressModel(
    var address: String,
    var city: String,
    var doorNumber: String,
    var pincode: Int,
    var state: String
) {

    override fun toString(): String {
        return doorNumber + "\n" + address + "\n" + city + "\n" + state + "\n" + pincode
    }

}