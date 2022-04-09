package com.example.algoliademo1.data.source.local.entity

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class ItemCountConverter {

    @TypeConverter
    fun stringToMap(data: String): ItemCount{
        val gson = Gson()
        val type: Type = object : TypeToken<ItemCount>() {}.type
        return gson.fromJson<ItemCount>(data, type)
    }

    @TypeConverter
    fun mapToString(itemCount: ItemCount): String{
        val gson = Gson()
        return gson.toJson(itemCount)
    }
}