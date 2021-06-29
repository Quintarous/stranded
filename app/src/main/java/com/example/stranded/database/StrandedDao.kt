package com.example.stranded.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.stranded.chatpage.Line

@Dao
interface StrandedDao {

    //insert methods
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTestSaveData(newSaveData: UserSave)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTestScriptLines(scriptLine: List<ScriptLine>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTestPromptLines(promptLine: List<PromptLine>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTestTriggers(trigger: List<Trigger>)

    //get methods
    @Query("SELECT * FROM UserSave")
    fun getUserSave(): LiveData<UserSave>
    @Query("SELECT * FROM ScriptLine WHERE sequence = :sequence")
    suspend fun getScriptLines(sequence: Int): List<Line>
    @Query("SELECT * FROM PromptLine WHERE sequence = :sequence")
    suspend fun getPromptLines(sequence: Int): List<PromptLine>
    @Query("SELECT * FROM `Trigger` WHERE sequence = :sequence")
    suspend fun getTriggers(sequence: Int): List<Trigger>
}