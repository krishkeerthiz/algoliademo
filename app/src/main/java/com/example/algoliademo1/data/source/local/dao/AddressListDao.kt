package com.example.algoliademo1.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.algoliademo1.data.source.local.entity.AddressList
import kotlinx.coroutines.flow.Flow

@Dao
interface AddressListDao {

    @Query("SELECT address_id FROM address_list WHERE user_id = :userId")
    suspend fun getAddresses(userId: String) : List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(addressList: AddressList)

}