package com.example.algoliademo1.data.source.local.entity

import android.util.Log
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.Serializable
import java.lang.reflect.Type
import java.util.*

class StringListConverter {

//    @TypeConverter
//    fun toList(value: String) = Json.decodeFromString<List<String>>(value)
//
//    @TypeConverter
//    fun fromArrayList(list: List<String>) = Json.encodeToString(list)
//    @TypeConverter
//    fun fromString(value: String): List<String> {
//        return value.split(',').map{
//            it
//        }
//    }
//
//    @TypeConverter
//    fun fromArrayList(list: List<String>): String {
//        return list.joinToString(separator = ",")
//    }
//    @TypeConverter
//    fun stringToStringList(data: String?): List<String?>?{
//        val gson = Gson()
//            if(data == null){
//                return Collections.emptyList()
//            }
//        val type: Type = object : TypeToken<List<String?>?>() {}.type
//        val x = gson.fromJson<List<String?>?>(data, type)
//      //  Log.d("Type Converter", x.toString())
//        return x
//    }
//
//    @TypeConverter
//    fun stringListToString(stringList: List<String?>?): String{
//        val gson = Gson()
//        return gson.toJson(stringList)
//    }

    @TypeConverter
    fun stringToStringList(data: String?): List<String?>?{
        val gson = Gson()

        return gson.fromJson(data, Array<String>::class.java).asList()
    }

    @TypeConverter
    fun stringListToString(stringList: List<String?>?): String{
        val gson = Gson()
        return gson.toJson(stringList)
    }

//    @TypeConverter
//    fun fromString(value: String?): ArrayList<String?>? {
//        val listType = object : TypeToken<ArrayList<String?>?>() {}.type
//        return Gson().fromJson(value, listType)
//    }
//
//    @TypeConverter
//    fun fromArrayList(list: ArrayList<String?>?): String? {
//        val gson = Gson()
//        return gson.toJson(list)
//    }
}