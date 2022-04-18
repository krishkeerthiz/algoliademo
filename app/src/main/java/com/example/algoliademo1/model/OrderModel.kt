package com.example.algoliademo1.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference

class
OrderModel {
    var address: DocumentReference? = null
    var date: Timestamp? = null
    var orderItems: DocumentReference? = null

    constructor(address: DocumentReference, date: Timestamp, orderItems: DocumentReference) {
        this.address = address
        this.date = date
        this.orderItems = orderItems
    }

    constructor()

}