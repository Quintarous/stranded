package com.example.stranded

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.stranded.database.StrandedDB
import com.example.stranded.database.StrandedDao
import com.example.stranded.database.UserSave
import com.example.stranded.database.getDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class Repository @Inject constructor(@ApplicationContext private val context: Context) {

    private val db: StrandedDB = getDatabase(context)
    private val dao: StrandedDao = db.getDao()

    val userSave: LiveData<UserSave> = dao.getUserSave()

    suspend fun updateUserSaveData(saveData: UserSave) = dao.insertTestSaveData(saveData)
}