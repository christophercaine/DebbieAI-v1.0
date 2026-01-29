package com.debbiedoesdetails.app.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Converters {
    private val gson = Gson()
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return gson.toJson(value ?: emptyList<String>())
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return try {
            gson.fromJson(value, object : TypeToken<List<String>>() {}.type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromStringMap(value: Map<String, String>?): String {
        return gson.toJson(value ?: emptyMap<String, String>())
    }

    @TypeConverter
    fun toStringMap(value: String): Map<String, String> {
        return try {
            gson.fromJson(value, object : TypeToken<Map<String, String>>() {}.type) ?: emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }
    }

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String {
        return value?.format(dateFormatter) ?: ""
    }

    @TypeConverter
    fun toLocalDateTime(value: String): LocalDateTime {
        return try {
            LocalDateTime.parse(value, dateFormatter)
        } catch (e: Exception) {
            LocalDateTime.now()
        }
    }
}