package com.debbiedoesit.antigravity.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface JobSiteDao {
    @Query("SELECT * FROM job_site_photos ORDER BY capturedAt DESC")
    fun getAllPhotos(): Flow<List<JobSitePhoto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertPhoto(photo: JobSitePhoto)

    @Delete suspend fun deletePhoto(photo: JobSitePhoto)
}

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts ORDER BY name ASC") fun getAllContacts(): Flow<List<Contact>>

    @Query("SELECT * FROM contacts WHERE id = :id") suspend fun getContactById(id: Long): Contact?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: Contact): Long

    @Update suspend fun updateContact(contact: Contact)

    @Delete suspend fun deleteContact(contact: Contact)

    // Interaction Queries
    @Query(
            "SELECT * FROM contact_interactions WHERE contactId = :contactId ORDER BY timestamp DESC"
    )
    fun getInteractionsForContact(contactId: Long): Flow<List<ContactInteraction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInteraction(interaction: ContactInteraction): Long
}

@Database(
        entities = [JobSitePhoto::class, Contact::class, ContactInteraction::class],
        version = 2,
        exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AntigravityDatabase : RoomDatabase() {
    abstract fun jobSiteDao(): JobSiteDao
    abstract fun contactDao(): ContactDao

    companion object {
        @Volatile private var INSTANCE: AntigravityDatabase? = null

        fun getDatabase(context: android.content.Context): AntigravityDatabase {
            return INSTANCE
                    ?: synchronized(this) {
                        val instance =
                                Room.databaseBuilder(
                                                context.applicationContext,
                                                AntigravityDatabase::class.java,
                                                "antigravity_database"
                                        )
                                        .build()
                        INSTANCE = instance
                        instance
                    }
        }
    }
}
