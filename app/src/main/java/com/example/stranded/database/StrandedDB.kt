package com.example.stranded.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities =
        [ScriptLine::class, PromptLine::class, UserSave::class, Trigger::class, PromptResult::class],
        version = 6,
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
                //.createFromAsset("stranded_database.db")
                .fallbackToDestructiveMigration()
                .enableMultiInstanceInvalidation()
                .build()
        }
        return INSTANCE
    }
}