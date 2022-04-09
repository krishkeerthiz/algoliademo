package com.example.algoliademo1.data.source.local.entity

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class IntListConverter {

    @TypeConverter
    fun stringToIntList(data: String): List<Int>{
        val gson = Gson()
        val type: Type = object : TypeToken<List<Int>>() {}.type
        return gson.fromJson<List<Int>>(data, type)
    }

    @TypeConverter
    fun intListToString(intList: List<Int>): String{
        val gson = Gson()
        return gson.toJson(intList)
    }
}