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

    val userSave: LiveData<UserSave> = dao.getLiveDataUserSave()
    //this is a synchronous blocking method for the PowerOnNotificationWorker
    fun getUserSave(): UserSave = dao.getUserSave()

    suspend fun getSequence(sequence: Int): Sequence {
        val scriptLines = dao.getScriptLines(sequence)
        val promptLines = dao.getPromptLines(sequence)
        val triggers = dao.getTriggers(sequence)

        val sets = createSetsList(promptLines)

        return Sequence(scriptLines, sets, triggers)
    }

    //methods for retrieving from and adding to and clearing the PromptResult table
    suspend fun getPromptResults() = dao.getPromptResults()
    suspend fun insertPromptResult(result: Int) = dao.insertPromptResult(PromptResult(0, result))
    suspend fun clearPromptResult() = dao.clearPromptResults()

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

        val scriptLineList = getScriptLineList1()

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

    suspend fun updateUserSaveData(saveData: UserSave) = dao.insertSaveData(saveData)
    fun noSuspendUpdateUserSaveData(saveData: UserSave) = dao.noSuspendInsertSaveData(saveData)

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
            ScriptLine(0, 1, "console", "--- SHUTTING POWER GATE ---", 0, "end"),


            //SEQUENCE 2


            //1-11
            ScriptLine(0, 2, "console", "--- BOOT PROCESS INITIATING ---", 2),
            ScriptLine(0, 2, "console", "--- BOOT SEQUENCE FAILURE ---", 3),
            ScriptLine(0, 2, "console", "---PARTS RETURNED NULL---", 4),
            ScriptLine(0, 2, "console", "ANLR1437_long_term_memory_module", 5),
            ScriptLine(0, 2, "console", "TAISHI943211_optical_sensor_module", 6),
            ScriptLine(0, 2, "console", "VERITECH88_high_capacity_battery_module", 7),
            ScriptLine(0, 2, "console", "--- REBOOTING IN SAFE MODE ---", 8),
            ScriptLine(0, 2, "console", "--- BOOT SEQUENCE SUCCESSFUL ---", 9),
            ScriptLine(0, 2, "script", "Come on... turn on you son of a-", 10),
            ScriptLine(0, 2, "script", "YES!", 11),
            ScriptLine(0, 2, "script", "Ladies and gentlemen... we have power!", 1, "prompt"),

            //12-15
            ScriptLine(0, 2, "script", "Nope.", 13),
            ScriptLine(0, 2, "script", "I came up here above the clouds to get you some sun and now here you are.", 14),
            ScriptLine(0, 2, "script", "It rose an hour or so ago and you've just now returned from the deep.", 15),
            ScriptLine(0, 2, "script", "Can you see how much juice you've got at the moment?", 2, "prompt"),

            //16-17
            ScriptLine(0, 2, "script", "I dunno I'm not the AI remember.", 17),
            ScriptLine(0, 2, "script", "Can you like query the system or something?", 3, "prompt"),

            //18
            ScriptLine(0, 2, "script", "...", 4, "prompt"),

            //19
            ScriptLine(0, 2, "script", "...", 5, "prompt"),

            //20
            ScriptLine(0, 2, "script", "Why are you talking like that?", 6, "prompt"),

            //21-24
            ScriptLine(0, 2, "script", "Well you don't do it like that stupid!", 22),
            ScriptLine(0, 2, "script", "I've never heard an AI speak out loud when doing stuff.", 23),
            ScriptLine(0, 2, "script", "They always just process it in the background.", 24),
            ScriptLine(0, 2, "script", "So you can't do anything at all in the background?", 7, "prompt"),

            //25
            ScriptLine(0, 2, "script", "Dang, your a very weird AI.", 8, "prompt"),

            //26
            ScriptLine(0, 2, "script", "Alright smartypants then what are you.", 9, "prompt"),

            //27-31
            ScriptLine(0, 2, "script", "Wow the silence is deafening.", 28),
            ScriptLine(0, 2, "script", "This is exactly my point what else could you possibly be.", 29),
            ScriptLine(0, 2, "script", "Sure you may not be able to do normal AI stuff but there must be a reason for it.", 30),
            ScriptLine(0, 2, "script", "I mean a lot parts were clearly missing from your main board when I looked at it.", 31),
            ScriptLine(0, 2, "script", "Maybe if we can replace them you'll get some of your functionality back.", 10, "prompt"),

            //32-38
            ScriptLine(0, 2, "script", "Ha! Your funny robot.", 33),
            ScriptLine(0, 2, "script", "Fingers are overrated anyway. Mine are still gross from making breakfast.", 34),
            ScriptLine(0, 2, "script", "...", 35),
            ScriptLine(0, 2, "script", "\uD83C\uDFB5Hmmm\uD83C\uDFB5", 36),
            ScriptLine(0, 2, "script", "\uD83C\uDFB5Hmm. Hmm. Hmmm.\uD83C\uDFB5", 37),
            ScriptLine(0, 2, "script", "I'll just strap you to my pack like so...", 38),
            ScriptLine(0, 2, "script", "Alright all packed up and ready to climb back down to soggy earth.", 11, "prompt"),

            //39
            ScriptLine(0, 2, "script", "Hey It's not your fault.", 30),

            //40-41
            ScriptLine(0, 2, "script", "Well I still think your an AI. Just a weird one who doesn't know anything and can't do much.", 41),
            ScriptLine(0, 2, "script", "Granted it's not entirely your fault since someone clearly took a lot of parts from your main board.", 31),

            //42-43
            ScriptLine(0, 2, "script", "That is correct it is a towering hulking monolith of an \"if\".", 43),
            ScriptLine(0, 2, "script", "But an \"if\" nonetheless so it's still technically possible.", 34),

            //44-51
            ScriptLine(0, 2, "script", "Oh right I forgot you can't see.", 45),
            ScriptLine(0, 2, "script", "We're on top of a building right now In an old city they called \"NEOM\" or \"The Line\".", 46),
            ScriptLine(0, 2, "script", "I'm walking across a plank to the fire escape stairwell of the neighboring building.", 47),
            ScriptLine(0, 2, "script", "This fire escape is the only really intact part of it left. Which is why I used it to climb up here above the cloud layer.", 48),
            ScriptLine(0, 2, "script", "We're pretty high up so it's gonna be a while before I make it down to the ground again.", 49),
            ScriptLine(0, 2, "script", "...", 50),
            ScriptLine(0, 2, "script", "...", 51),
            ScriptLine(0, 2, "script", "So do you have a name?", 12, "prompt"),

            //52-54
            ScriptLine(0, 2, "script", "I dunno just thought I'd ask.", 53),
            ScriptLine(0, 2, "script", "I mean...", 54),
            ScriptLine(0, 2, "script", "I have to call you something!", 13, "prompt"),

            //55-56
            ScriptLine(0, 2, "script", "I'm Seyuvi.", 56),
            ScriptLine(0, 2, "script", "Dr. Seyuvi Khouri.", 14, "prompt"),

            //57-59
            ScriptLine(0, 2, "script", "Yes but not the medical kind.", 58),
            ScriptLine(0, 2, "script", "I have a Doctorate in neural network integration maintenance.", 59),
            ScriptLine(0, 2, "script", "Or at least I did. They probably revoked it when I was exiled...", 15, "prompt"),

            //60-61
            ScriptLine(0, 2, "script", "It is...", 61),
            ScriptLine(0, 2, "script", "It's a long story, do you want the short or the long version?", 16, "prompt"),

            //62-65
            ScriptLine(0, 2, "script", "Well, they found out I was feeding weather survey data from Earth to some of the AI's that were under my maintenance order.", 63),
            ScriptLine(0, 2, "script", "I wanted to understand what was happening down there on the planet.", 64),
            ScriptLine(0, 2, "script", "When they caught me stealing AI compute resources for unapproved purposes they did what they do to all criminals.", 65),
            ScriptLine(0, 2, "script", "Exiled me from the Orbital. I was shot down in a Needle to look for-", 17, "prompt"),

            //66-69
            ScriptLine(0, 2, "script", "Wait how old are you?", 67),
            ScriptLine(0, 2, "script", "Do you remember the corporation names from the part models you were missing?", 68),
            ScriptLine(0, 2, "console", "--- DATA QUERY RESULT ---", 69),
            ScriptLine(0, 2, "console", "--- ANLR | Taishi | Veritech ---", 18, "prompt"),

            //70
            ScriptLine(0, 2, "script", "Haha! Sounds like your finally learning how to use some of your compute queries! That was a very AI style answer.", 19, "prompt"),

            //71-74
            ScriptLine(0, 2, "script", "But wow. You are waaaaay older than I thought. You must be an old world prototype or something.", 72),
            ScriptLine(0, 2, "script", "Remarkable. I didn't know anything as advanced as an AI was even tried back then...", 73),
            ScriptLine(0, 2, "script", "Anyway those parts you listed are all old world corporations. I've seen some of their advertising since I landed.", 74),
            ScriptLine(0, 2, "script", "But this means you don't know about what's going on with the weather then do you?", 20, "prompt"),

            //75-85
            ScriptLine(0, 2, "script", "Long story short... It's completely borked.", 76),
            ScriptLine(0, 2, "script", "There are different flavors depending on where you go but it's generally bad and unstable everywhere.", 77),
            ScriptLine(0, 2, "script", "Like here for example. This region gets extremely heavy rainfall regularly.", 78),
            ScriptLine(0, 2, "script", "And when I say heavy I mean everything floods up to 20-50ft within like 10 or 20 minutes.", 79),
            ScriptLine(0, 2, "script", "The rain falls in sheets so thick it will beat you down off your feet and drown you if your not careful.", 80),
            ScriptLine(0, 2, "script", "It's almost killed me a few times but I'm good at predicting it now.", 81),
            ScriptLine(0, 2, "script", "When the drops start falling you have a few minutes to get up high or in a waterproof place. Because it fills up the first 4 or so floors of buildings.", 82),
            ScriptLine(0, 2, "script", "Luckily it only comes in quick violent spurts than stops for a while. That's when it's safe to move around.", 83),
            ScriptLine(0, 2, "script", "Nobody knows how it got this way, but one thing I know for certain is it wasn't always like this.", 84),
            ScriptLine(0, 2, "script", "That's why they built the Orbital. To get off the planet, save themselves from catastrophe.", 85),
            ScriptLine(0, 2, "script", "And no one has been down here since the Needles were designed.", 21, "prompt"),

            //86-90
            ScriptLine(0, 2, "script", "Needles are what they use to take people from the Orbital to the planet.", 87),
            ScriptLine(0, 2, "script", "They're large, long, and thin metal \"needles\" for lack of a better term with space for a person and some supplies.", 88),
            ScriptLine(0, 2, "script", "They drop them from the Orbital into deep oceans, with a single person inside.", 89),
            ScriptLine(0, 2, "script", "Their shape is what lets them survive reentry and collision with the water.", 90),
            ScriptLine(0, 2, "script", "They \"needle\" their way in! And once your in the airbags deploy to slow you down and eventually float you to the surface.", 22, "prompt"),

            //91
            ScriptLine(0, 2, "script", "Mm-hmm!", 23, "prompt"),

            //92
            ScriptLine(0, 2, "script", "Yes...", 24, "prompt"),

            //93
            ScriptLine(0, 2, "script", "Yup!", 25, "prompt"),

            //94-97
            ScriptLine(0, 2, "script", "Unfortunately.", 95),
            ScriptLine(0, 2, "script", "...", 96),
            ScriptLine(0, 2, "script", "Alright my machine friend I answered your questions now your gonna answer one of mine.", 97),
            ScriptLine(0, 2, "script", "What's your name?", 26, "prompt"),

            //98-99
            ScriptLine(0, 2, "console", "--- DATA QUERY: MY NAME ---", 99),
            ScriptLine(0, 2, "console", "--- MAZU ---", 27, "prompt"),

            //100
            ScriptLine(0, 2, "script", "You sound surprised.", 28, "prompt"),

            //101-104
            ScriptLine(0, 2, "script", "That's a strange name...", 102),
            ScriptLine(0, 2, "script", "Mazu.", 103),
            ScriptLine(0, 2, "script", "I wonder if it means anything.", 104),
            ScriptLine(0, 2, "console", "--- INTERNAL POWER CRITICALLY LOW ---", 29, "prompt"),

            //105-110
            ScriptLine(0, 2, "script", "And I'm almost at the bottom of this stairwell.", 106),
            ScriptLine(0, 2, "console", "--- INITIATING SHUTDOWN ---", 107),
            ScriptLine(0, 2, "script", "\uD83C\uDFB5Hmm-hm-hmmm\uD83C\uDFB5", 108),
            ScriptLine(0, 2, "console", "--- SHUTDOWN IN PROGRESS ---", 109),
            ScriptLine(0, 2, "script", "\uD83C\uDFB5Hmmm\uD83C\uDFB5", 110),
            ScriptLine(0, 2, "console", "--- CLOSING POWER GATE ---", 0, "end"),


            //SEQUENCE 3


            //1-12
            ScriptLine(0, 3, "console", "--- BOOT SEQUENCE INITIATING ---", 2),
            ScriptLine(0, 3, "console", "--- BOOT SEQUENCE FAILURE --- ", 3),
            ScriptLine(0, 3, "console", "--- PARTS RETURNED NULL --- ", 4),
            ScriptLine(0, 3, "console", "ANLR1437_long_term_memory_module", 5),
            ScriptLine(0, 3, "console", "TAISHI943211_optical_sensor_module", 6),
            ScriptLine(0, 3, "console", "VERITECH88_high_capacity_battery_module", 7),
            ScriptLine(0, 3, "console", "--- REBOOTING IN SAFE MODE ---", 8),
            ScriptLine(0, 3, "console", "--- BOOT SUCCESSFUL ---", 9),
            ScriptLine(0, 3, "script", "Wow. I'm glad the sun came out.", 10),
            ScriptLine(0, 3, "script", "It's warming up these waters nicely.", 11),
            ScriptLine(0, 3, "script", "Don't jinx it yuvi!", 12),
            ScriptLine(0, 3, "script", "You'll bring the rain back.", 1, "prompt"),

            //13-17
            ScriptLine(0, 3, "script", "Oh!", 14),
            ScriptLine(0, 3, "script", "Yea... Sorry.", 15),
            ScriptLine(0, 3, "script", "Old habits die hard I guess.", 16),
            ScriptLine(0, 3, "script", "I've been alone for so long that...", 17),
            ScriptLine(0, 3, "script", "Never mind.", 2, "prompt"),

            //18-19
            ScriptLine(0, 3, "script", "Well well well.", 19),
            ScriptLine(0, 3, "script", "Look who's back from the dead once more.", 3, "prompt"),

            //20-23
            ScriptLine(0, 3, "script", "It's normal to feel weird after shutdowns.", 21),
            ScriptLine(0, 3, "script", "AI aren't built to be shut off as frequently as you've been.", 22),
            ScriptLine(0, 3, "script", "In fact I'd bet these shutoffs are the main reason you have so little control of your compute resources.", 23),
            ScriptLine(0, 3, "script", "No time to familiarize and configure the artificial neural network link.", 4, "prompt"),

            //24-31
            ScriptLine(0, 3, "script", "Sorry!", 25),
            ScriptLine(0, 3, "script", "It's been a long time since I've been able to work on or with live AI.", 26),
            ScriptLine(0, 3, "script", "I miss it sometimes...", 27),
            ScriptLine(0, 3, "script", "I miss them too.", 28),
            ScriptLine(0, 3, "script", "Those AI I worked with back on the Orbital were my friends I'd like to think.", 29),
            ScriptLine(0, 3, "script", "Heh-heh, they were the only friends I had if I'm being honest!", 30),
            ScriptLine(0, 3, "script", "...", 31),
            ScriptLine(0, 3, "script", "Anyway we're on a boat right now.", 5, "prompt"),

            //32-38
            ScriptLine(0, 3, "script", "Because this part of NEOM is flooded. DUH.", 33),
            ScriptLine(0, 3, "script", "We're in the western quarter of The Line next to the sea.", 34),
            ScriptLine(0, 3, "script", "I guess when they built this city the ocean must have been lower or something.", 35),
            ScriptLine(0, 3, "script", "...", 36),
            ScriptLine(0, 3, "script", "It's actually pretty nice out here at the moment.", 37),
            ScriptLine(0, 3, "script", "The waters aren't always this calm, and it's even more rare to get a break in the clouds like this!", 38),
            ScriptLine(0, 3, "script", "Hopefully I didn't just jinx us.", 6, "prompt"),

            //39-42
            ScriptLine(0, 3, "script", "Umm...", 40),
            ScriptLine(0, 3, "script", "So, I need to get through to the other side of this city to find-", 41),
            ScriptLine(0, 3, "script", "Oh no.", 42),
            ScriptLine(0, 3, "script", "I jinxed it!", 7, "prompt"),

            //43
            ScriptLine(0, 3, "script", "I jinxed it! I jinxed it! I freaking jinxed it!", 8, "prompt"),

            //44-48
            ScriptLine(0, 3, "script", "The rain!", 45),
            ScriptLine(0, 3, "script", "That was just the calm before the storm!", 46),
            ScriptLine(0, 3, "script", "Oh this is bad, I'm isolated here.", 47),
            ScriptLine(0, 3, "script", "Need to find shelter, need to climb.", 48),
            ScriptLine(0, 3, "script", "...", 9, "prompt"),

            //49-64
            ScriptLine(0, 3, "script", "HA!", 50),
            ScriptLine(0, 3, "script", "What do you think I'm doing.", 51),
            ScriptLine(0, 3, "script", "...", 52),
            ScriptLine(0, 3, "script", "Crap! The currents picking up. I can't go sideways to get to the buildings.", 53),
            ScriptLine(0, 3, "script", "Wait... I think I see a sky bridge up ahead.", 54),
            ScriptLine(0, 3, "script", "I might be able to reach it if I time my jump correctly!", 55),
            ScriptLine(0, 3, "script", "Okay yuvi you got this.", 56),
            ScriptLine(0, 3, "script", "Easy peasy.", 57),
            ScriptLine(0, 3, "script", "Just gotta wait for it...", 58),
            ScriptLine(0, 3, "script", "Oooh boy the boats moving pretty fast.", 59),
            ScriptLine(0, 3, "script", "Argh. Come on. Come on.", 60),
            ScriptLine(0, 3, "script", "da da da da da NOW!", 61),
            ScriptLine(0, 3, "script", "Huuuurgh!", 62),
            ScriptLine(0, 3, "script", "Don't slip, don't slip, please don't slip yuvi.", 63),
            ScriptLine(0, 3, "script", "...", 64),
            ScriptLine(0, 3, "script", "*pant* holy *pant* crap *pant*", 10, "prompt"),

            //65
            ScriptLine(0, 3, "script", "Hey if I die you'll probably die with me!", 51),

            //66-68
            ScriptLine(0, 3, "script", "Yes *pant* Mazu *pant* we're not *pant* dead yet *pant*", 67),
            ScriptLine(0, 3, "script", "Hooo man! Okay.", 68),
            ScriptLine(0, 3, "script", "Time to go. Your still not high enough yuvi.", 11, "prompt"),

            //69(nice)-70
            ScriptLine(0, 3, "script", "Made the jump *pant* seems like *pant* embellishment *pant*", 70),
            ScriptLine(0, 3, "script", "Maybe, *pant* barely didn't fall *pant* would be more accurate.", 67),

            //71-74
            ScriptLine(0, 3, "script", "Like I said this morning. 4-5 floors up or a place with an air pocket.", 72),
            ScriptLine(0, 3, "script", "...", 73),
            ScriptLine(0, 3, "script", "Wow the carpets here are actually quite nice.", 74),
            ScriptLine(0, 3, "script", "At least the dry parts are.", 12, "prompt"),

            //75-80
            ScriptLine(0, 3, "script", "This isn't my first rodeo Mazu.", 76),
            ScriptLine(0, 3, "script", "These storms are frequent here.", 77),
            ScriptLine(0, 3, "script", "I just got caught unprepared out on the water.", 78),
            ScriptLine(0, 3, "script", "It's not a good place to be when the rain comes.", 79),
            ScriptLine(0, 3, "script", "...", 80),
            ScriptLine(0, 3, "script", "ah poop.", 13, "prompt"),

            //81-84
            ScriptLine(0, 3, "script", "Calm your tits Mazu.", 82),
            ScriptLine(0, 3, "script", "I've never met an AI so hung up on the fact that they're not human.", 83),
            ScriptLine(0, 3, "script", "The ones I used to maintain would always comment that they felt sorry for me.", 84),
            ScriptLine(0, 3, "script", "Having to inconvenience myself with eating and sleeping! *laughs*", 79),

            //85-87
            ScriptLine(0, 3, "script", "Stairs out.", 86),
            ScriptLine(0, 3, "script", "Well I guess I could try climbing up the elevator shaft.", 87),
            ScriptLine(0, 3, "script", "Though I'd prefer not to if I'm being perfectly honest.", 14, "prompt"),

            //88-90
            ScriptLine(0, 3, "script", "Don't you sass me!", 89),
            ScriptLine(0, 3, "script", "I could drop you at any time you know.", 90),
            ScriptLine(0, 3, "script", "So you better be nice to me!", 15, "prompt"),

            //91-95
            ScriptLine(0, 3, "script", "You are actually.", 92),
            ScriptLine(0, 3, "script", "Hmm.", 93),
            ScriptLine(0, 3, "script", "This elevator shaft looks fairly climbable surprisingly.", 94),
            ScriptLine(0, 3, "script", "...", 95),
            ScriptLine(0, 3, "script", "This one's actually not as bad as they usually are.", 19, "prompt"),

            //96-98
            ScriptLine(0, 3, "script", "And what would you know about risk evaluation?", 97),
            ScriptLine(0, 3, "script", "I only woke you up yesterday, and you remember basically nothing.", 98),
            ScriptLine(0, 3, "script", "PLUS you can't even see at the moment!", 16, "prompt"),

            //99-100
            ScriptLine(0, 3, "script", "...", 100),
            ScriptLine(0, 3, "script", "...", 17, "prompt"),

            //101
            ScriptLine(0, 3, "script", "Yup!", 18, "prompt"),

            //102-109
            ScriptLine(0, 3, "script", "Ehhhhh...", 103),
            ScriptLine(0, 3, "script", "Maybe not daily.", 104),
            ScriptLine(0, 3, "script", "But more than you'd think I guess.", 105),
            ScriptLine(0, 3, "script", "...", 106),
            ScriptLine(0, 3, "script", "Whoof!", 107),
            ScriptLine(0, 3, "script", "Okay made it. Let's just take a quick peek out this window here.", 108),
            ScriptLine(0, 3, "script", "Mmmmm... yea we should be fine. I think we're high enough.", 109),
            ScriptLine(0, 3, "console", "--- INTERNAL POWER CRITICALLY LOW ---", 20, "prompt"),

            //110-111
            ScriptLine(0, 3, "console", "--- INITIATING SHUTDOWN ---", 111),
            ScriptLine(0, 3, "script", "Sorry I was watching the rain through the window. What did you say?", 21, "prompt"),

            //112-113
            ScriptLine(0, 3, "script", "Oh...", 113),
            ScriptLine(0, 3, "script", "Mazu?", 22, "prompt"),

            //114-119
            ScriptLine(0, 3, "console", "--- SHUTDOWN IN PROGRESS ---", 115),
            ScriptLine(0, 3, "script", "Thanks for talking to me.", 116),
            ScriptLine(0, 3, "script", "Things seem less scary with you around to talk to.", 117),
            ScriptLine(0, 3, "console", "--- CLOSING POWER GATE ---", 118),
            ScriptLine(0, 3, "script", "The light blinks out...", 119),
            ScriptLine(0, 3, "script", "And I'm alone again...", 0, "end")
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


            //SEQUENCE 2


            //set 1
            PromptLine(0, 2, 1, "So I'm not dead after all.", 12),

            //set 2
            PromptLine(0, 2, 2, "How would I do that?", 16),

            //set 3
            PromptLine(0, 2, 3, "Uh... internal battery status. ", 18),

            //set 4
            PromptLine(0, 2, 4, "Onboard power.", 19),

            //set 5
            PromptLine(0, 2, 5, "Onboard power status.", 20),

            //set 6
            PromptLine(0, 2, 6, "I'm querying the system?", 21),

            //set 7
            PromptLine(0, 2, 7, "...", 25),

            //set 8
            PromptLine(0, 2, 8, "Probably because I'm not an AI!", 26),
            PromptLine(0, 2, 8, "Wow ok.", 39),

            //set 9
            PromptLine(0, 2, 9, "...", 27),
            PromptLine(0, 2, 9, "I don't know.", 40),
            PromptLine(0, 2, 9, "Well clearly not an AI.", 40),

            //set 10
            PromptLine(0, 2, 10, "I would cross my fingers but I don't have any.", 32),
            PromptLine(0, 2, 10, "That's a veeeery big if.", 42),

            //set 11
            PromptLine(0, 2, 11, "Where are we?", 44),

            //set 12
            PromptLine(0, 2, 12, "I can't remember anything else. Why would I remember that?", 52),

            //set 13
            PromptLine(0, 2, 13, "What are you called?", 55),

            //set 14
            PromptLine(0, 2, 14, "Doctor?", 57),

            //set 15
            PromptLine(0, 2, 15, "Exiled?", 60),

            //set 16
            PromptLine(0, 2, 16, "It's not like I have anything else to do.", 62),

            //set 17
            PromptLine(0, 2, 17, "Hold on! Orbital? Needle? What are you talking about?", 66),

            //set 18
            PromptLine(0, 2, 18, "ANLR, Taishi, Veritech.", 70),

            //set 19
            PromptLine(0, 2, 19, "That was... interesting.", 71),

            //set 20
            PromptLine(0, 2, 20, "No...", 75),

            //set 21
            PromptLine(0, 2, 21, "What is a Needle?", 86),

            //set 22
            PromptLine(0, 2, 22, "So you were in a space station you call the \"Orbital\".", 91),

            //set 23
            PromptLine(0, 2, 23, "And they dropped you to earth in this \"Needle\"...", 92),

            //set 24
            PromptLine(0, 2, 24, "Because you were exiled.", 93),

            //set 25
            PromptLine(0, 2, 25, "And the weather is \"borked\".", 94),

            //set 26
            PromptLine(0, 2, 26, "...", 98),

            //set 27
            PromptLine(0, 2, 27, "Mazu?", 100),

            //set 28
            PromptLine(0, 2, 28, "I am.", 101),

            //set 29
            PromptLine(0, 2, 29, "Power's almost gone again.", 105),


            //SEQUENCE 3


            //set 1
            PromptLine(0, 3, 1, "Still talking to yourself I see.", 13),
            PromptLine(0, 3, 1, "Hello again.", 18),

            //set 2
            PromptLine(0, 3, 2, "I don't think I'll ever get used to those shutdowns.", 20),

            //set 3
            PromptLine(0, 3, 3, "I'll never get used to that...", 20),

            //set 4
            PromptLine(0, 3, 4, "What on earth are you talking about?", 24),

            //set 5
            PromptLine(0, 3, 5, "Because?", 32),

            //set 6
            PromptLine(0, 3, 6, "So what are you doing here?", 39),

            //set 7
            PromptLine(0, 3, 7, "What?", 43),

            //set 8
            PromptLine(0, 3, 8, "What's happening?!", 44),

            //set 9
            PromptLine(0, 3, 9, "Well try not to die I guess!", 49),
            PromptLine(0, 3, 9, "Good luck.", 65),

            //set 10
            PromptLine(0, 3, 10, "Have you succeeded in not dying yet?", 66),
            PromptLine(0, 3, 10, "I'm assuming we made the jump.", 69),

            //set 11
            PromptLine(0, 3, 11, "How high do we need to go?", 71),

            //set 12
            PromptLine(0, 3, 12, "You sound remarkably calm for someone who nearly drowned.", 75),
            PromptLine(0, 3, 12, "That carpet sounds like it would be very nice... If I could feel it!", 81),

            //set 13
            PromptLine(0, 3, 13, "What is it?", 85),

            //set 14
            PromptLine(0, 3, 14, "Sucks to suck! Hop to it.", 88),
            PromptLine(0, 3, 14, "Sounds like a risky play.", 96),

            //set 15
            PromptLine(0, 3, 15, "I'm waterproof though right?", 91),

            //set 16
            PromptLine(0, 3, 16, "Do you want my advice or not?", 99),

            //set 17
            PromptLine(0, 3, 17, "Your climbing the shaft aren't you.", 101),

            //set 18
            PromptLine(0, 3, 18, "Of course you are...", 95),

            //set 19
            PromptLine(0, 3, 19, "Is climbing elevator shafts a daily occurance for you?", 102),

            //set 20
            PromptLine(0, 3, 20, "Here we go again...", 110),

            //set 21
            PromptLine(0, 3, 21, "Almost out of juice again.", 112),

            //set 22
            PromptLine(0, 3, 22, "Yes?", 114)
        )

        //PromptLine(0, 1, 0, "", 0),
    }

    fun getTriggerList1(): List<Trigger> {
        return mutableListOf(
            Trigger(0, 1, 24, "script", "animation", R.drawable.g_meter_up_animation, loop=false, oneAndDone = true),
            Trigger(0, 1, 56, "script", "animation", R.drawable.g_walk_animation, true),


            //SEQUENCE 2


            Trigger(0, 2, 9, "script", "sound", R.raw.fire_birds, true),
            Trigger(0, 2, 44, "script", "sound", null),
            Trigger(0, 2, 0, "script", "animation", R.drawable.g_walk_animation, true),


            //SEQUENCE 3


            Trigger(0, 3, 9, "script", "sound", R.raw.waves, true),
            Trigger(0, 3, 9, "script", "animation", R.drawable.g_walk_animation, true),
            Trigger(0, 3, 51, "script", "sound", R.raw.river, true),
            Trigger(0, 3, 60, "script", "animation", R.drawable.g_meter_down_animation, true),
            Trigger(0, 3, 63, "script", "animation", null),
            Trigger(0, 3, 68, "script", "animation", R.drawable.g_walk_animation, true),
            Trigger(0, 3, 68, "script", "sound", R.raw.rain_window, true),
            Trigger(0, 3, 79, "script", "animation", null),
            Trigger(0, 3, 94, "script", "animation", R.drawable.g_down_small_animation, true),
            Trigger(0, 3, 99, "script", "animation", R.drawable.g_down_small_animation, true),
            Trigger(0, 3, 106, "script", "animation", R.drawable.g_walk_animation, true),
            Trigger(0, 3, 108, "script", "animation", null),
        )
    }

}