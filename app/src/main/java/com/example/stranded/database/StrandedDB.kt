package com.example.stranded.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities =
        [SequenceScripts::class, SequencePrompts::class, UserSave::class, Triggers::class],
        version = 1,
        exportSchema = true
)
abstract class StrandedDB: RoomDatabase() {
    abstract fun getDao(): StrandedDao
}

private lateinit var INSTANCE: StrandedDB

//TODO switch this to an real database as opposed to an in memory one when finished testing
fun getDatabase(context: Context): StrandedDB {
    synchronized(StrandedDB::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.inMemoryDatabaseBuilder(
                context,
                StrandedDB::class.java
            )
                .fallbackToDestructiveMigration()
                .enableMultiInstanceInvalidation()
                .build()
        }
        return INSTANCE
    }
}