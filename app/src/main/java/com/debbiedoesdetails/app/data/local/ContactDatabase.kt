package com.debbiedoesdetails.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Contact::class, Address::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ContactDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun addressDao(): AddressDao

    companion object {
        @Volatile
        private var instance: ContactDatabase? = null

        fun getInstance(context: Context): ContactDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    ContactDatabase::class.java,
                    "contacts_db"
                ).fallbackToDestructiveMigration().build().also { instance = it }
            }
        }
    }
}