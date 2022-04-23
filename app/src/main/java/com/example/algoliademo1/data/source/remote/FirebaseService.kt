package com.example.algoliademo1.data.source.remote

import android.util.Log
import com.example.algoliademo1.model.ProductModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

object FirebaseService {
    private const val TAG = "FirebaseService"
    var userId = Firebase.auth.currentUser!!.uid

    fun refreshUserId() {
        userId = Firebase.auth.currentUser!!.uid
        Log.d(TAG, "refreshUserId: user id refreshed $userId")
    }

    fun testGetProductReference(id: String): DocumentReference {
        val db = FirebaseFirestore.getInstance()
        val docId = id.removeSurrounding("\"", "\"")
        val documentRef = db.collection("products4").document(docId)
        return documentRef
    }

    fun getAddressReference(): CollectionReference {
        val db = FirebaseFirestore.getInstance()
        val addressCollectionRef = db.collection("address")

        return addressCollectionRef
    }

    fun getAddressesReference(): DocumentReference {
        val db = FirebaseFirestore.getInstance()
        val addressDocumentRef = db.collection("addresses").document(userId)

        return addressDocumentRef
    }


    fun getCartReference(): DocumentReference {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("cart").document(userId)
        return docRef
    }

    fun getWishlistReference(): DocumentReference {
        val userId = Firebase.auth.currentUser!!.uid
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("wishlist").document(userId)
        return docRef
    }

    fun getOrderItemsReference(): CollectionReference {
        val db = FirebaseFirestore.getInstance()
        return db.collection("orderItems")
    }

    fun addToCart(productId: String, price: Float, quantity: Int = 1) {
        val docRef = getCartReference()
        // in firestore map<productId, quantity>

        docRef.set(
            mapOf(
                "products" to hashMapOf(productId to FieldValue.increment(quantity.toLong())),
                "total" to FieldValue.increment(price.toDouble())
            ), SetOptions.merge()
        ).addOnSuccessListener {
            Log.d(TAG, "Product Added to cart, total updated")
        }
            .addOnFailureListener {
                Log.d(TAG, "Failed to add product to cart")
            }
    }

    fun removeFromCart(productId: String, price: Float) {
        val docRef = getCartReference()
        // in firestore map<productId, quantity>

        docRef.update(
            mapOf(
                "products.$productId" to FieldValue.delete(),
                "total" to FieldValue.increment(-price.toDouble())
            )
        ).addOnSuccessListener {
            Log.d(TAG, "Product removed from cart, total updated")
        }
            .addOnFailureListener {
                Log.d(TAG, "Failed to remove product to cart")
            }
    }

    fun addToWishlist(productId: String) {
        val docRef = getWishlistReference()
        // in firestore map<productId, quantity>

        docRef.set(
            mapOf(
                "products" to FieldValue.arrayUnion(productId),
            ), SetOptions.merge()
        ).addOnSuccessListener {
            Log.d(TAG, "Product Added to wishlist")
        }
            .addOnFailureListener {
                Log.d(TAG, "Failed to add product to wishlist")
            }
    }

    fun removeFromWishlist(productId: String) {
        val docRef = getWishlistReference()
        docRef.update(
            "products", FieldValue.arrayRemove(productId),
        ).addOnSuccessListener {
            Log.d(TAG, "Product removed from wishlist")
        }
            .addOnFailureListener {
                Log.d(TAG, "Failed to remove product from wishlist")
            }
    }

    fun incrementProductQuantity(productId: String, price: Float) {
        val docRef = getCartReference()
        docRef.update(
            mapOf(
                "products.$productId" to FieldValue.increment(1),
                "total" to FieldValue.increment(price.toDouble())
            )
        ).addOnSuccessListener {
            Log.d(TAG, "Product increment to cart, total updated")
        }
            .addOnFailureListener {
                Log.d(TAG, "Failed to increment product to cart")
            }
    }

    fun getProduct(id: String): ProductModel? {
        val db = FirebaseFirestore.getInstance()
        val docId = id.removeSurrounding("\"", "\"")
        val documentRef = db.collection("products4").document(docId)
        // val documentRef = db.collection("products4")

        //  documentRef.where(Firebase.firestore.document().id , 'in', listOf<String>("1", "2"))
        Log.d(TAG, "Document ref finished")

        val taskDocSnap = documentRef.get()
        Log.d(TAG, "task doc snap finished")
//         val docSnap = taskDocSnap.result
//         Log.d(TAG, "Document Snapshot finished")

        var productModel: ProductModel? = null

        taskDocSnap.addOnCompleteListener {
            productModel = it.result?.toObject<ProductModel>()
        }
        taskDocSnap.addOnSuccessListener { docSnap ->
            productModel = docSnap.toObject<ProductModel>()
            Log.d(TAG, "Data received ${productModel?.name}")
        }.addOnFailureListener { exception ->
            Log.d(TAG, "Error getting document: ", exception)
            productModel = null
        }


        return productModel
    }

    fun getOrderReference(): CollectionReference {
        val db = FirebaseFirestore.getInstance()
        return db.collection("order")
    }

    fun getOrdersDocumentReference(): DocumentReference {
        val db = FirebaseFirestore.getInstance()
        return db.collection("orders").document(userId)
    }
}