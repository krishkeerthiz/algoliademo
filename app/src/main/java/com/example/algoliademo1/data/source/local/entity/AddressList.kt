package com.example.algoliademo1.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Can get all addresses belongs to individual users
@Entity(tableName = "address_list")
data class AddressList(
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "address_id") val addressId: String
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "address_list_id")
    var addressListId: Int = 0
}
