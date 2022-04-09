package com.example.algoliademo1.data.source.local.entity

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject
import java.lang.reflect.Type

class ListConverter {
    @TypeConverter
    fun stringToStringList(data: String): List<String>{
        val jsonArray = JSONArray(data)
//        val type: Type = object : TypeToken<List<String>>() {}.type
//        return gson.fromJson<List<String>>(data, type)
        val returnStrList = ArrayList<String>(jsonArray.length())
        for(i in 0 until jsonArray.length()-1){
            returnStrList.add(jsonArray[i].toString())
        }
        return returnStrList
    }

    @TypeConverter
    fun stringListToString(stringList: List<String>): String{
        val gson = Gson()
        return gson.toJson(stringList)
    }
}