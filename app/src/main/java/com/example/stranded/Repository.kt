package com.example.stranded

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.stranded.chatpage.Sequence
import com.example.stranded.database.*
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class Repository @Inject constructor(@ApplicationContext private val context: Context) {

    private val db: StrandedDB = getDatabase(context)
    private val dao: StrandedDao = db.getDao()

    val userSave: LiveData<UserSave> = dao.getUserSave()

    suspend fun getSequence(sequence: Int): Sequence {
        val scriptLines = dao.getScriptLines(sequence)
        val promptLines = dao.getPromptLines(sequence)
        val triggers = dao.getTriggers(sequence)

        val sets = createSetsList(promptLines)

        return Sequence(scriptLines, sets, triggers)
    }

    suspend fun insertTestScriptLines() {
        val list = mutableListOf<ScriptLine>()

        for (i in (1..10)) {
            var type = "script"
            var next = i + 1
            var nextType = "script"

            when (i) {
                1 -> type = "console"

                3 -> {
                    next = 1
                    nextType = "prompt"
                }
                5 -> {
                    next = 2
                    nextType = "prompt"
                }
                10 -> nextType = "end"
            }

            val line =
                ScriptLine(0, 1, type, "script line $i", next, nextType)

            list.add(line)
        }

        dao.insertTestScriptLines(list)
    }

    suspend fun insertTestPromptLines() {
        val list = listOf(
            PromptLine(0, 1, 1, "goes to line 4", 4),
            PromptLine(0, 1, 1, "goes to line 4 and triggers animation", 4),
            PromptLine(0, 1, 1, "goes to prompt set 2", 2, "prompt"),
            PromptLine(0, 1, 2, "prompt line 4", 6),
            PromptLine(0, 1, 2, "prompt line 5", 6)
        )

        dao.insertTestPromptLines(list)
    }

    //TODO fix these for testing purposes!
    suspend fun insertTestTriggers() {
        val list = mutableListOf<Trigger>(
            Trigger(0, 1, 3, "script", "sound", R.raw.fire_birds, loop = true, oneAndDone = false),
            Trigger(0, 1, 4, "script", "sound", R.raw.rain1, loop = false, oneAndDone = true),
            Trigger(0, 1, 2, "prompt", "animation", R.drawable.g_meter_up_animation, loop = true, oneAndDone = false),
            Trigger(0, 1, 9, "script", "animation", null, null, null),
            Trigger(0, 1, 9, "script", "sound", null, null, null)
        )

        dao.insertTestTriggers(list)
    }

    suspend fun updateUserSaveData(saveData: UserSave) = dao.insertTestSaveData(saveData)
}