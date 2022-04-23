package com.example.algoliademo1.model

class AddressModel {
    var address: String = ""
    var city: String = ""
    var doorNumber: String = ""
    var pincode: Int = 600001
    var state: String = ""

    constructor() // Firebase needs empty constructor to instantiate

    override fun toString(): String {
        return doorNumber + "\n" + address + "\n" + city + "\n" + state + "\n" + pincode
    }

    constructor(address: String, city: String, doorNumber: String, pincode: Int, state: String) {
        this.address = address
        this.city = city
        this.doorNumber = doorNumber
        this.pincode = pincode
        this.state = state
    }
}