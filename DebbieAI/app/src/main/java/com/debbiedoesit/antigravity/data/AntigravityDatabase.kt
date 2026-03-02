package com.debbiedoesit.antigravity.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface JobSiteDao {
    @Query("SELECT * FROM job_site_photos ORDER BY capturedAt DESC")
    fun getAllPhotos(): Flow<List<JobSitePhoto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: JobSitePhoto)

    @Delete
    suspend fun deletePhoto(photo: JobSitePhoto)
}

@Database(entities = [JobSitePhoto::class], version = 1)
@TypeConverters(Converters::class)
abstract class AntigravityDatabase : RoomDatabase() {
    abstract fun jobSiteDao(): JobSiteDao

    companion object {
        @Volatile
        private var INSTANCE: AntigravityDatabase? = null

        fun getDatabase(context: android.content.Context): AntigravityDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AntigravityDatabase::class.java,
                    "antigravity_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
