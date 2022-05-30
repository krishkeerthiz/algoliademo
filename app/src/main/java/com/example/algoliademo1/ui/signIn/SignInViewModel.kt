package com.example.algoliademo1.ui.signIn

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.algoliademo1.data.source.remote.FirebaseService
import com.example.algoliademo1.data.source.repository.CartRepository
import kotlinx.coroutines.launch

class SignInViewModel(context: Context) : ViewModel() {

    private val repository = CartRepository.getRepository(context)

    fun initializeCart(){
        viewModelScope.launch {
            Log.d(TAG, "initializeCart: onStart: signin initialization called")
            val cart = repository.getCart(FirebaseService.userId)

            if (cart == null) {
                Log.d(TAG, "initializeCart: onStart: signin cart is null ")
                repository.createCartEntry(FirebaseService.userId)
            }
        }
    }
}

class SignInViewModelFactory(private val context: Context) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SignInViewModel(context) as T
    }
}