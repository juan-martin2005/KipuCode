package com.kipucode.data.local.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ListConverters{
    private val gson = Gson()

    // De String (BD - Entity) a Lista (List<String>)
    @TypeConverter
    fun fromString(value: String?): List<String> {
        if (value == null) return emptyList()
        val listType = object : TypeToken<List<String>>() {}.type

        return gson.fromJson(value, listType)
    }

    // De Lista (List<String>) a String (BD - Entity)
    @TypeConverter
    fun fromList(list: List<String>?): String =
        gson.toJson(list)

    // De Map a String
    @TypeConverter
    fun fromMapString(value: String?): Map<String, Int> {
        if (value == null) return emptyMap()
        val mapType = object : TypeToken<Map<String, Int>>() {}.type
        return gson.fromJson(value, mapType)
    }

    // De String a Map
    @TypeConverter
    fun fromMap(map: Map<String, Int>?): String {
        return gson.toJson(map ?: emptyMap<String, Int>())
    }
}