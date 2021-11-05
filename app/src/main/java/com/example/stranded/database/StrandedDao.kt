package com.example.stranded.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface StrandedDao {

    //insert methods
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSaveData(newSaveData: UserSave)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun noSuspendInsertSaveData(newSaveData: UserSave)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTestScriptLines(scriptLine: List<ScriptLine>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTestPromptLines(promptLine: List<PromptLine>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTestTriggers(trigger: List<Trigger>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPromptResult(input: PromptResult)

    //get methods
    @Query("SELECT * FROM UserSave")
    fun getLiveDataUserSave(): LiveData<UserSave>
    @Query("SELECT * FROM UserSave")
    suspend fun getUserSave(): UserSave
    @Query("SELECT * FROM UserSave")
    fun getUserSaveBlocking(): UserSave
    @Query("SELECT * FROM ScriptLine WHERE sequence = :sequence")
    suspend fun getScriptLines(sequence: Int): List<ScriptLine>
    @Query("SELECT * FROM PromptLine WHERE sequence = :sequence")
    suspend fun getPromptLines(sequence: Int): List<PromptLine>
    @Query("SELECT * FROM `Trigger` WHERE sequence = :sequence")
    suspend fun getTriggers(sequence: Int): List<Trigger>
    @Query("SELECT * FROM PromptResult")
    suspend fun getPromptResults(): List<PromptResult>

    //clearing the promptChoices table
    @Query("DELETE FROM PromptResult")
    suspend fun clearPromptResults()
}