package com.example.algoliademo1.data.source.local

import android.content.Context
import android.os.strictmode.InstanceCountViolation
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.algoliademo1.data.source.local.dao.*
import com.example.algoliademo1.data.source.local.entity.Address
import com.example.algoliademo1.data.source.local.entity.AddressList
import com.example.algoliademo1.data.source.local.entity.Categories
import com.example.algoliademo1.data.source.local.entity.Product
import com.example.algoliademo1.data.source.local.entity.Cart
import com.example.algoliademo1.data.source.local.entity.CartItems
import com.example.algoliademo1.data.source.local.entity.Order
import com.example.algoliademo1.data.source.local.entity.OrderItems
import com.example.algoliademo1.data.source.local.entity.Wishlist
import com.example.algoliademo1.data.source.local.entity.Orders
import com.example.algoliademo1.util.DateConverter
import com.example.algoliademo1.util.JsonUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

@Database(entities = [Product::class, Address::class, AddressList::class, CartItems::class, Cart::class,
                     Categories::class, Order::class, OrderItems::class, Orders::class, Wishlist::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class ShoppingRoomDatabase : RoomDatabase(){

    abstract fun productsDao() : ProductsDao
    abstract fun addressDao() : AddressDao
    abstract fun addressListDao() : AddressListDao
    abstract fun cartDao() : CartDao
    abstract fun cartItemsDao() : CartItemsDao
    abstract fun categoriesDao() : CategoriesDao
    abstract fun orderDao() : OrderDao
    abstract fun orderItemsDao() : OrderItemsDao
    abstract fun ordersDao() : OrdersDao
    abstract fun wishlistDao() : WishlistDao

    private class ShoppingDatabaseCallback(private val scope: CoroutineScope, val context: Context)
        : RoomDatabase.Callback(){
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.productsDao(), database.categoriesDao(), context)
                }
            }
        }

        suspend fun populateDatabase(productsDao: ProductsDao, categoriesDao: CategoriesDao, context: Context){
            val obj = JSONObject(JsonUtil.loadJSONFromAsset(context))
            val productsArray = obj.getJSONArray("products")

            for(i in 0 until productsArray.length()){
                val productDetail = productsArray.getJSONObject(i)

                addProductsToDatabase(i, productDetail, productsDao)
                addProductCategoriesToDatabase(i, productDetail.getJSONArray("categories"), categoriesDao)
            }
        }

        private suspend fun addProductsToDatabase(index: Int, productDetail: JSONObject, productsDao: ProductsDao){
            val productId = index.toString()
            val brand = productDetail.getString("brand")
            val description = productDetail.getString("description")
            val freeShipping = productDetail.getBoolean("free_shipping")
            val image = productDetail.getString("image")
            val name = productDetail.getString("name")
            val objectId = productDetail.getString("objectID")
            val popularity = productDetail.getInt("popularity")
            val price = productDetail.getDouble("price").toFloat()
            val priceRange = productDetail.getString("price_range")
            val rating = productDetail.getInt("rating")
            val type = productDetail.getString("type")
            val url = productDetail.getString("url")

            val product = Product(productId, brand, description, freeShipping, image, name, objectId, popularity, price
                , priceRange, rating, type, url)
            productsDao.insert(product)
        }

        private suspend fun addProductCategoriesToDatabase(index: Int, categories: JSONArray, categoriesDao: CategoriesDao){
            for(i in 0 until categories.length()){
                val category = categories.get(i) as String

                categoriesDao.insert(Categories(index.toString(), category))
            }
        }

    }

    companion object{
        @Volatile
        private var INSTANCE: ShoppingRoomDatabase? = null

        fun getDatabase(context: Context
        , scope: CoroutineScope): ShoppingRoomDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ShoppingRoomDatabase::class.java,
                    "shopping_database"
                )
                    .addCallback(ShoppingDatabaseCallback(scope, context))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
