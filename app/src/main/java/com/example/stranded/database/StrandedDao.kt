package com.example.stranded.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StrandedDao {

    //insert methods
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTestSaveData(newSaveData: UserSave)

    //get methods
    @Query("SELECT * FROM UserSave")
    fun getUserSave(): LiveData<UserSave>
}