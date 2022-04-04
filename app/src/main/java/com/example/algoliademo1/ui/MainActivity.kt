package com.example.algoliademo1.ui

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.algoliademo1.R
import com.example.algoliademo1.data.source.local.entity.Order
import com.example.algoliademo1.ui.address.AddressFragment
import com.example.algoliademo1.ui.cart.CartFragment
import com.example.algoliademo1.ui.filters.FacetFragment
import com.example.algoliademo1.ui.orders.OrdersFragment
import com.example.algoliademo1.ui.orderdetail.OrderDetailFragment
import com.example.algoliademo1.ui.products.ProductFragment
import com.example.algoliademo1.ui.prductdetail.ProductDetailFragment
import com.example.algoliademo1.ui.wishlist.WishlistFragment
import com.google.firebase.firestore.DocumentReference

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: 1")
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate:2 ")
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate:3 ")
        showProductFragment()
    }

    fun showProductFragment() {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.container, ProductFragment())
            .commit()
    }

    fun showFacetFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, FacetFragment())
            .addToBackStack("facet")
            .commit()
    }

    fun showProductDetailFragment(id: String){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, ProductDetailFragment(id))
            .addToBackStack("Product detail")
            .commit()
    }

    fun showCartFragment(){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, CartFragment())
            .addToBackStack("Cart items")
            .commit()
    }

    fun showWishlistFragment(){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, WishlistFragment())
            .addToBackStack("Cart items")
            .commit()
    }

    fun showAddressFragment(){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, AddressFragment())
            .addToBackStack("Address fragment")
            .commit()
    }

    fun showOrdersFragment(){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, OrdersFragment())
            .addToBackStack("Orders fragment")
            .commit()
    }

    fun showOrderDetailFragment(order: Order) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, OrderDetailFragment(order))  // replace document reference with order
            .addToBackStack("Order Detail fragment")
            .commit()

    }

    override fun onSupportNavigateUp(): Boolean {
        supportFragmentManager.popBackStack()
        return super.onSupportNavigateUp()
    }
}
