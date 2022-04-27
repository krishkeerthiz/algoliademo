package com.example.algoliademo1.data.source.remote

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

object FirebaseService {

    var userId = Firebase.auth.currentUser!!.uid

    fun refreshUserId() {
        userId = Firebase.auth.currentUser!!.uid
    }
}