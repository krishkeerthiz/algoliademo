package com.example.algoliademo1.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.algoliademo1.R
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
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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

    fun showOrderDetailFragment(orderDocumentReference: DocumentReference) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, OrderDetailFragment(orderDocumentReference))
            .addToBackStack("Order Detail fragment")
            .commit()

    }

    override fun onSupportNavigateUp(): Boolean {
        supportFragmentManager.popBackStack()
        return super.onSupportNavigateUp()
    }
}
