package com.example.stranded

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.stranded.chatpage.Sequence
import com.example.stranded.database.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class Repository @Inject constructor(@ApplicationContext private val context: Context) {

    // getting the database and dao object
    private val db: StrandedDB = getDatabase(context)
    private val dao: StrandedDao = db.getDao()


    /**
     * Depending on the use case, the repository provides 3 different ways of retrieving the UserSave.
     */
    // getting UserSave via a flow
    val userSaveFlow = dao.getUserSaveFlow()
    // getting UserSave via a suspend function
    suspend fun getUserSave(): UserSave = dao.getUserSave()
    // getting UserSave via a blocking synchronous function
    fun getUserSaveBlocking(): UserSave = dao.getUserSaveBlocking()


    /**
     * Grabs data from all the tables and combines them to make a sequence object
     */
    suspend fun getSequence(sequence: Int): Sequence {
        val scriptLines = dao.getScriptLines(sequence)
        val promptLines = dao.getPromptLines(sequence)
        val triggers = dao.getTriggers(sequence)

        val sets = createSetsList(promptLines)

        return Sequence(scriptLines, sets, triggers)
    }


    // retrieving the prompt results
    suspend fun getPromptResults() = dao.getPromptResults()
    // inserting a prompt result
    suspend fun insertPromptResult(result: Int) = dao.insertPromptResult(PromptResult(0, result))
    // clearing the prompt result table
    suspend fun clearPromptResult() = dao.clearPromptResults()


    // updating the UserSave
    suspend fun updateUserSaveData(saveData: UserSave) = dao.insertSaveData(saveData)
    // synchronous blocking version
    fun noSuspendUpdateUserSaveData(saveData: UserSave) = dao.noSuspendInsertSaveData(saveData)
}