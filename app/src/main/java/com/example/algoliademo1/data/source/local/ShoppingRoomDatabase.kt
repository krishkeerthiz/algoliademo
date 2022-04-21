package com.example.algoliademo1.data.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.algoliademo1.data.source.local.dao.*
import com.example.algoliademo1.data.source.local.entity.*
import com.example.algoliademo1.util.DateConverter
import com.example.algoliademo1.util.JsonUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

@Database(
    entities = [Product::class, Address::class, AddressList::class, CartItems::class, Cart::class,
        Categories::class, Order::class, OrderItems::class, Orders::class, Wishlist::class, ProductRatings::class],
    version = 1, exportSchema = false
)
@TypeConverters(
    DateConverter::class
)

abstract class ShoppingRoomDatabase : RoomDatabase() {

    abstract fun productsDao(): ProductsDao
    abstract fun addressDao(): AddressDao
    abstract fun addressListDao(): AddressListDao
    abstract fun cartDao(): CartDao
    abstract fun cartItemsDao(): CartItemsDao
    abstract fun categoriesDao(): CategoriesDao
    abstract fun orderDao(): OrderDao
    abstract fun orderItemsDao(): OrderItemsDao
    abstract fun ordersDao(): OrdersDao
    abstract fun wishlistDao(): WishlistDao
    abstract fun productRatingsDao(): ProductRatingsDao

    private class ShoppingDatabaseCallback(
        private val scope: CoroutineScope,
        val context: Context
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(
                        database.productsDao(),
                        database.categoriesDao(),
                        database.wishlistDao(),
                        database.cartDao(),
                        context
                    )
                }
            }
        }

        suspend fun populateDatabase(
            productsDao: ProductsDao,
            categoriesDao: CategoriesDao,
            wishlistDao: WishlistDao,
            cartDao: CartDao,
            context: Context
        ) {

            scope.launch {
                val obj = JSONObject(JsonUtil.loadJSONFromAsset(context))
                val productsArray = obj.getJSONArray("products4")

                for (i in 0 until productsArray.length()) {
                    val productDetail = productsArray.getJSONObject(i)

                    addProductsToDatabase(i, productDetail, productsDao)
                    addProductCategoriesToDatabase(
                        i,
                        productDetail.getJSONArray("categories"),
                        categoriesDao
                    )

                }
            }.join()

        }

        private suspend fun addProductsToDatabase(
            index: Int,
            productDetail: JSONObject,
            productsDao: ProductsDao
        ) {
            var product: Product?

            productDetail.apply {
                val brand = getString("brand")
                val description = getString("description")
                val freeShipping = getBoolean("free_shipping")
                val image = getString("image")
                val name = getString("name")
                val objectId = getString("objectID")
                val popularity = getInt("popularity")
                val price = getDouble("price").toFloat()
                val priceRange = getString("price_range")
                val rating = getInt("rating")
                val type = getString("type")
                val url = getString("url")

                val productId = index.toString()

                product = Product(
                    productId,
                    brand,
                    description,
                    freeShipping,
                    image,
                    name,
                    objectId,
                    popularity,
                    price,
                    priceRange,
                    rating,
                    type,
                    url
                )

            }
            productsDao.insert(product!!)
        }

        private suspend fun addProductCategoriesToDatabase(
            index: Int,
            categories: JSONArray,
            categoriesDao: CategoriesDao
        ) {
            for (i in 0 until categories.length()) {
                val category = categories.get(i) as String

                categoriesDao.insert(Categories(index.toString(), category))
            }
        }


    }

    companion object {
        @Volatile
        private var INSTANCE: ShoppingRoomDatabase? = null

        fun getDatabase(
            context: Context, scope: CoroutineScope
        ): ShoppingRoomDatabase {
            return INSTANCE ?: synchronized(this) {
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
