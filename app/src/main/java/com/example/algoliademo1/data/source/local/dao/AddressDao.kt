package com.example.algoliademo1.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.algoliademo1.data.source.local.entity.Address
import com.example.algoliademo1.data.source.local.entity.AddressList

@Dao
interface AddressDao {

    @Query("SELECT * FROM address WHERE address_id = :addressId")
    suspend fun getAddress(addressId: String) : Address

//    @Query("DELETE FROM address WHERE address_id = :addressId")
//    suspend fun deleteAddress(addressId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(address: Address)
}