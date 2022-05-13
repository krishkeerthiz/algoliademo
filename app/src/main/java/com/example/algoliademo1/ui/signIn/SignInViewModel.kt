package com.example.algoliademo1.ui.signIn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.algoliademo1.data.source.remote.FirebaseService
import com.example.algoliademo1.data.source.repository.CartRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignInViewModel : ViewModel() {

    private val repository = CartRepository //.getRepository()

    fun initializeCart(){
        viewModelScope.launch(Dispatchers.IO) {
            val cart = repository.getCart(FirebaseService.userId)

            if (cart == null)
                repository.createCartEntry(FirebaseService.userId)
        }
    }

}