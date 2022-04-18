package com.example.algoliademo1.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.algoliademo1.data.source.local.entity.Address

@Dao
interface AddressDao {

    @Query("SELECT * FROM address WHERE address_id = :addressId")
    suspend fun getAddress(addressId: String): Address

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(address: Address)
}