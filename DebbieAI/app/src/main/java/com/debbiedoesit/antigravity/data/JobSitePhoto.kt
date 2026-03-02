package com.debbiedoesit.antigravity.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

enum class PhotoType {
    BEFORE, AFTER, PROGRESS, DAMAGE, MATERIAL, MEASUREMENT, GENERAL
}

@Entity(tableName = "job_site_photos")
data class JobSitePhoto(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val filePath: String,
    val projectId: String,
    val capturedAt: Long = System.currentTimeMillis(),
    val detectedObjects: List<String> = emptyList(),
    val extractedText: List<String> = emptyList(),
    val aiDescription: String = "",
    val photoType: PhotoType = PhotoType.GENERAL,
    val gpsLat: Double? = null,
    val gpsLng: Double? = null
)

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromPhotoType(value: PhotoType): String {
        return value.name
    }

    @TypeConverter
    fun toPhotoType(value: String): PhotoType {
        return PhotoType.valueOf(value)
    }
}
