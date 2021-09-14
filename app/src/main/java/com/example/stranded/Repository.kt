package com.example.stranded

import android.content.Context
import android.util.Log
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

    //methods for retrieving from and adding to the PromptResult table
    suspend fun getPromptResults() = dao.getPromptResults()
    suspend fun insertPromptResult(result: Int) = dao.insertPromptResult(PromptResult(0, result))

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

        dao.insertTestScriptLines(getScriptLineList1())
    }

    suspend fun insertTestPromptLines() {
        val list = listOf(
            PromptLine(0, 1, 1, "goes to line 4", 4),
            PromptLine(0, 1, 1, "goes to line 4 and triggers animation", 4),
            PromptLine(0, 1, 1, "goes to prompt set 2", 2, "prompt"),
            PromptLine(0, 1, 2, "trigger one and done", 6),
            PromptLine(0, 1, 2, "prompt line 5", 6)
        )

        dao.insertTestPromptLines(getPromptLineList1())
    }

    //TODO fix these for testing purposes!
    suspend fun insertTestTriggers() {
        val list = mutableListOf<Trigger>(
            Trigger(0, 1, 3, "script", "sound", R.raw.fire_birds, loop = true, oneAndDone = false),
            Trigger(0, 1, 3, "script", "animation", R.drawable.g_walk_animation, loop = true, oneAndDone = false),
            Trigger(0, 1, 4, "script", "sound", R.raw.waves, loop = true, oneAndDone = false),
            Trigger(0, 1, 2, "prompt", "animation", R.drawable.g_meter_up_animation, loop = true, oneAndDone = false),
            Trigger(0, 1, 4, "prompt", "animation", R.drawable.g_wobble_animation, loop = false, oneAndDone = true),
            Trigger(0, 1, 9, "script", "animation", null),
            Trigger(0, 1, 9, "script", "sound", null)
        )

        dao.insertTestTriggers(getTriggerList1())
    }

    suspend fun updateUserSaveData(saveData: UserSave) = dao.insertTestSaveData(saveData)

    fun getScriptLineList1(): List<ScriptLine> {
        return mutableListOf(
            //1-23
            ScriptLine(0, 1, "console", "---POWERING BOOT DRIVE---", 2, "script"),
            ScriptLine(0, 1, "console", "---CHECKING DRIVE INTEGRITY---", 3, "script"),
            ScriptLine(0, 1, "console", "---DRIVE SUCCESSFULLY VERIFIED---", 4, "script"),
            ScriptLine(0, 1, "console", "---BOOT PROCESSING---", 5, "script"),
            ScriptLine(0, 1, "script", "...", 6, "script"),
            ScriptLine(0, 1, "script", "...", 7, "script"),
            ScriptLine(0, 1, "console", "---BOOT SEQUENCE FAILURE---", 8, "script"),
            ScriptLine(0, 1, "console", "---PARTS RETURNED NULL---", 9, "script"),
            ScriptLine(0, 1, "console", "ANLR1437_long_term_memory_module", 10, "script"),
            ScriptLine(0, 1, "console", "TAISHI943211_optical_sensor_module", 11, "script"),
            ScriptLine(0, 1, "console", "VERITECH88_high_capacity_battery_module", 12, "script"),
            ScriptLine(0, 1, "console", "---REBOOTING IN SAFE MODE---", 13, "script"),
            ScriptLine(0, 1, "console", "---BOOT SEQUENCE SUCCESSFUL---", 14, "script"),
            ScriptLine(0, 1, "script", "There must be something here...", 15, "script"),
            ScriptLine(0, 1, "script", "Something something something always looking for something.", 16, "script"),
            ScriptLine(0, 1, "script", "Today it's bottles tomorrow it'll be something else, and whatever it is it'll be stupidly hard to find.", 17, "script"),
            ScriptLine(0, 1, "script", "Because nothings ever easy...", 18, "script"),
            ScriptLine(0, 1, "script", "Wait...", 19, "script"),
            ScriptLine(0, 1, "script", "What are you I wonder?", 20, "script"),
            ScriptLine(0, 1, "script", "It looks kinda like an AI cartridge back home... but like crappier.", 21, "script"),
            ScriptLine(0, 1, "console", "---MOVEMENT DETECTED---", 22, "script"),
            ScriptLine(0, 1, "console", "---CALIBRATING GYRO---", 23, "script"),
            ScriptLine(0, 1, "script", "Huh heavier than it looks.", 1, "prompt"),

            //24-27
            ScriptLine(0, 1, "script", "Ahh!", 25),
            ScriptLine(0, 1, "console", "---IMPACT LOAD SUSTAINED 1.4G---", 26),
            ScriptLine(0, 1, "script", "What the what!? It's alive!", 27),
            ScriptLine(0, 1, "script", "Holy crap holy crap hoooly crap!", 2, "prompt"),

            //28-31
            ScriptLine(0, 1, "script", "I can't believe it! An actual live AI cartridge right here.", 29),
            ScriptLine(0, 1, "script", "I've never seen one like this before though.", 30),
            ScriptLine(0, 1, "script", "You must be an old model. Like really old.", 31),
            ScriptLine(0, 1, "script", "Were you sitting here this whole time? How long have you been active?", 3, "prompt"),

            //32-33
            ScriptLine(0, 1, "script", "So if you got turned on a few minutes ago. Where did you come from?", 33),
            ScriptLine(0, 1, "script", "Do you remember anything before just now?", 4, "prompt"),

            //34
            ScriptLine(0, 1, "script", "Hello? You there?", 5, "prompt"),

            //35-39
            ScriptLine(0, 1, "script", "Whoa...", 36),
            ScriptLine(0, 1, "script", "An AI. In an ancient looking cartridge I've never seen before.", 37),
            ScriptLine(0, 1, "script", "That has just turned on but has no memories whatsoever...", 38),
            ScriptLine(0, 1, "script", "This must be a weird surreal dream. I'm just talking to myself with extra steps.", 39),
            ScriptLine(0, 1, "script", "Maybe I am losing my mind...", 6, "prompt"),

            //40
            ScriptLine(0, 1, "script", "...", 7, "prompt"),

            //41
            ScriptLine(0, 1, "script", "What kind of parts?", 8, "prompt"),

            //42-45
            ScriptLine(0, 1, "script", "That's not a small part. If the cartridge your in is anything like modern ones...", 43),
            ScriptLine(0, 1, "script", "Oh no. Yea that's exposed battery wiring, looks like it was removed as well but much more cleanly whoever took this knew what they were doing.", 44),
            ScriptLine(0, 1, "script", "You won't be able to run for a long period of time since you'll only be on a much smaller onboard battery.", 45),
            ScriptLine(0, 1, "script", "As for how long you actually have I'm not sure we'll have to wait for it to die first. There are solar panels on here though so at least it's not finite.", 9, "prompt"),

            //46-49
            ScriptLine(0, 1, "script", "That's... messed up.", 47),
            ScriptLine(0, 1, "script", "Modern AI have onboard memory storage and it's inexorably tied to their operation. There is no way to tamper with it, it's unethical.", 48),
            ScriptLine(0, 1, "script", "I do remember reading about some early models requiring external capacity modules since there wasn't enough room onboard.", 49),
            ScriptLine(0, 1, "script", "But I suppose that explains why you can't remember anything huh?", 10, "prompt"),

            //50-51
            ScriptLine(0, 1, "script", "Oh you can't see me! That must be what this socket is for... or was for.", 51),
            ScriptLine(0, 1, "script", "It looks like it was ripped out. Not cleanly either.", 11, "prompt"),

            //52
            ScriptLine(0, 1, "script", "Hey look on the bright side buddy! Your coming with me whether you like it or not.", 12, "prompt"),

            //53-54
            ScriptLine(0, 1, "script", "True, true.", 55),
            ScriptLine(0, 1, "script", "I like your enthusiasm.", 55),

            //55-63
            ScriptLine(0, 1, "console", "--- MOTION DETECTED ---", 56),
            ScriptLine(0, 1, "script", "I'm stuffing you in my backpack by the way. Since your missing most of your sensors I figured you'd like to know.", 57),
            ScriptLine(0, 1, "script", "...", 58),
            ScriptLine(0, 1, "script", "...", 59),
            ScriptLine(0, 1, "script", "...", 60),
            ScriptLine(0, 1, "script", "Huh... this building was a lot easier to get into than to get out of.", 61),
            ScriptLine(0, 1, "script", "I wonder if... Hrghhh!", 62),
            ScriptLine(0, 1, "script", "Got it!", 63),
            ScriptLine(0, 1, "script", "Nice job Yuvi-", 13, "prompt"),

            //64-68
            ScriptLine(0, 1, "script", "Oh! Sorry I'm just talking to myself.", 65),
            ScriptLine(0, 1, "script", "Wait why am I apologizing to an AI?", 66),
            ScriptLine(0, 1, "script", "...", 67),
            ScriptLine(0, 1, "console", "--- INTERNAL POWER LOW ---", 68),
            ScriptLine(0, 1, "console", "--- BATTERY MODULE NOT DETECTED ---", 14, "prompt"),

            //69
            ScriptLine(0, 1, "script", "Doesn't surprise me.", 15, "prompt"),

            //70-74
            ScriptLine(0, 1, "script", "Well, when I manage to climb my way out of here I'm gonna find a spot to hole up for the night.", 71),
            ScriptLine(0, 1, "script", "And in the morning we'll see if those solar panels of yours still function.", 72),
            ScriptLine(0, 1, "script", "Or... I guess I'll see whether they work or not because you'll be asleep... or dead or whatever it is AI experience when they stop running.", 73),
            ScriptLine(0, 1, "script", "...", 74),
            ScriptLine(0, 1, "script", "Bit of a philosophical question that one.", 16, "prompt"),

            //75-81
            ScriptLine(0, 1, "script", "Hey I'm not your mom. What do you want a kiss goodnight or something?", 76),
            ScriptLine(0, 1, "console", "--- INTERNAL POWER CRITICALLY LOW ---", 77),
            ScriptLine(0, 1, "console", "--- INITIATING SHUTDOWN ---", 78),
            ScriptLine(0, 1, "script", "Hello you still there?", 79),
            ScriptLine(0, 1, "console", "--- SHUTDOWN IN PROGRESS ---", 80),
            ScriptLine(0, 1, "script", "Ah they're dead... or asleep you never know.", 81),
            ScriptLine(0, 1, "console", "--- SHUTTING POWER GATE ---", 0, "end")
        )

        //ScriptLine(0, 1, "script", "", 0),
    }

    fun getPromptLineList1(): List<PromptLine> {
        return mutableListOf(
            //sequence 1
            //set 1
            PromptLine(0, 1, 1, "Hello?", 24),
            PromptLine(0, 1, 1, "Did you just pick me up?", 24),
            PromptLine(0, 1, 1, "Are you talking to yourself?", 24),

            //set 2
            PromptLine(0, 1, 2, "Are you ok?", 28),
            PromptLine(0, 1, 2, "...", 28),

            //set 3
            PromptLine(0, 1, 3, "A few minutes at most.", 32),

            //set 4
            PromptLine(0, 1, 4, "...", 34),
            PromptLine(0, 1, 4, "Nope... Nothing.", 35),

            //set 5
            PromptLine(0, 1, 5, "I don't remember a single thing...", 35),

            //set 6
            PromptLine(0, 1, 6, "When I woke up it said there were modules missing.", 40),

            //set 7
            PromptLine(0, 1, 7, "It said they were returning null.", 41),

            //set 8
            PromptLine(0, 1, 8, "A high capacity battery.", 42),

            //set 9
            PromptLine(0, 1, 9, "Something called a long_term_memory_module.", 46),

            //set 10
            PromptLine(0, 1, 10, "Some sort of optical module.", 50),

            //set 11
            PromptLine(0, 1, 11, "...", 52),
            PromptLine(0, 1, 11, "Crap!", 52),

            //set 12
            PromptLine(0, 1, 12, "Not like I could do anything about it anyway.", 53),
            PromptLine(0, 1, 12, "Fine by me!", 54),

            //set 13
            PromptLine(0, 1, 13, "Who are you talking to?", 64),
            PromptLine(0, 1, 13, "...", 66),

            //set 14
            PromptLine(0, 1, 14, "I'm running out of power.", 69),

            //set 15
            PromptLine(0, 1, 15, "So what now?", 70),

            //set 16
            PromptLine(0, 1, 16, "Reassuring.", 75),
        )

        //PromptLine(0, 1, 0, "", 0),
    }

    fun getTriggerList1(): List<Trigger> {
        return mutableListOf(
            Trigger(0, 1, 24, "script", "animation", R.drawable.g_meter_up_animation, loop=false, oneAndDone = true),
            Trigger(0, 1, 56, "script", "animation", R.drawable.g_walk_animation, true)
        )
    }

    fun getScriptLineList2(): List<ScriptLine> {
        return listOf(

        )
    }
}