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

    // live data provider of the user save data
    val userSave: LiveData<UserSave> = dao.getLiveDataUserSave()
    // asynchronous non blocking method
    suspend fun getUserSave(): UserSave = dao.getUserSave()
    // synchronous blocking method for the PowerOnNotificationWorker
    fun getUserSaveBlocking(): UserSave = dao.getUserSaveBlocking()

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

    suspend fun insertTestTriggers() {
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
            ScriptLine(0, 1, "console", "---MOTION DETECTED---", 56),
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
            ScriptLine(0, 1, "console", "---INTERNAL POWER LOW---", 68),
            ScriptLine(0, 1, "console", "---BATTERY MODULE NOT DETECTED---", 14, "prompt"),

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
            ScriptLine(0, 1, "console", "---INTERNAL POWER CRITICALLY LOW---", 77),
            ScriptLine(0, 1, "console", "---INITIATING SHUTDOWN---", 78),
            ScriptLine(0, 1, "script", "Hello you still there?", 79),
            ScriptLine(0, 1, "console", "---SHUTDOWN IN PROGRESS---", 80),
            ScriptLine(0, 1, "script", "Ah they're dead... or asleep you never know.", 81),
            ScriptLine(0, 1, "console", "---SHUTTING POWER GATE---", 0, "end"),


            // SEQUENCE 2


            //1-11
            ScriptLine(0, 2, "console", "---BOOT PROCESS INITIATING---", 2),
            ScriptLine(0, 2, "console", "---BOOT SEQUENCE FAILURE---", 3),
            ScriptLine(0, 2, "console", "---PARTS RETURNED NULL---", 4),
            ScriptLine(0, 2, "console", "ANLR1437_long_term_memory_module", 5),
            ScriptLine(0, 2, "console", "TAISHI943211_optical_sensor_module", 6),
            ScriptLine(0, 2, "console", "VERITECH88_high_capacity_battery_module", 7),
            ScriptLine(0, 2, "console", "---REBOOTING IN SAFE MODE---", 8),
            ScriptLine(0, 2, "console", "---BOOT SEQUENCE SUCCESSFUL---", 9),
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
            ScriptLine(0, 2, "console", "---DATA QUERY RESULT---", 69),
            ScriptLine(0, 2, "console", "---ANLR | Taishi | Veritech---", 18, "prompt"),

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
            ScriptLine(0, 2, "console", "---DATA QUERY: MY NAME---", 99),
            ScriptLine(0, 2, "console", "---MAZU---", 27, "prompt"),

            //100
            ScriptLine(0, 2, "script", "You sound surprised.", 28, "prompt"),

            //101-104
            ScriptLine(0, 2, "script", "That's a strange name...", 102),
            ScriptLine(0, 2, "script", "Mazu.", 103),
            ScriptLine(0, 2, "script", "I wonder if it means anything.", 104),
            ScriptLine(0, 2, "console", "---INTERNAL POWER CRITICALLY LOW---", 29, "prompt"),

            //105-110
            ScriptLine(0, 2, "script", "And I'm almost at the bottom of this stairwell.", 106),
            ScriptLine(0, 2, "console", "---INITIATING SHUTDOWN---", 107),
            ScriptLine(0, 2, "script", "\uD83C\uDFB5Hmm-hm-hmmm\uD83C\uDFB5", 108),
            ScriptLine(0, 2, "console", "---SHUTDOWN IN PROGRESS---", 109),
            ScriptLine(0, 2, "script", "\uD83C\uDFB5Hmmm\uD83C\uDFB5", 110),
            ScriptLine(0, 2, "console", "---CLOSING POWER GATE---", 0, "end"),


            // SEQUENCE 3


            //1-12
            ScriptLine(0, 3, "console", "---BOOT SEQUENCE INITIATING---", 2),
            ScriptLine(0, 3, "console", "---BOOT SEQUENCE FAILURE--- ", 3),
            ScriptLine(0, 3, "console", "---PARTS RETURNED NULL--- ", 4),
            ScriptLine(0, 3, "console", "ANLR1437_long_term_memory_module", 5),
            ScriptLine(0, 3, "console", "TAISHI943211_optical_sensor_module", 6),
            ScriptLine(0, 3, "console", "VERITECH88_high_capacity_battery_module", 7),
            ScriptLine(0, 3, "console", "---REBOOTING IN SAFE MODE---", 8),
            ScriptLine(0, 3, "console", "---BOOT SUCCESSFUL---", 9),
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
            ScriptLine(0, 3, "console", "---INTERNAL POWER CRITICALLY LOW---", 20, "prompt"),

            //110-111
            ScriptLine(0, 3, "console", "---INITIATING SHUTDOWN---", 111),
            ScriptLine(0, 3, "script", "Sorry I was watching the rain through the window. What did you say?", 21, "prompt"),

            //112-113
            ScriptLine(0, 3, "script", "Oh...", 113),
            ScriptLine(0, 3, "script", "Mazu?", 22, "prompt"),

            //114-119
            ScriptLine(0, 3, "console", "---SHUTDOWN IN PROGRESS---", 115),
            ScriptLine(0, 3, "script", "Thanks for talking to me.", 116),
            ScriptLine(0, 3, "script", "Things seem less scary with you around to talk to.", 117),
            ScriptLine(0, 3, "console", "---CLOSING POWER GATE---", 118),
            ScriptLine(0, 3, "script", "The light blinks out...", 119),
            ScriptLine(0, 3, "script", "And I'm alone again...", 0, "end"),


            // SEQUENCE 4


            //1-13
            ScriptLine(0, 4, "console", "---BOOT SEQUENCE INITIATING---", 2),
            ScriptLine(0, 4, "console", "---BOOT SEQUENCE FAILURE---", 3),
            ScriptLine(0, 4, "console", "---PARTS RETURNED NULL---", 4),
            ScriptLine(0, 4, "console", "ANLR1437_long_term_memory_module", 5),
            ScriptLine(0, 4, "console", "TAISHI943211_optical_sensor_module", 6),
            ScriptLine(0, 4, "console", "VERITECH88_high_capacity_battery_module", 7),
            ScriptLine(0, 4, "console", "---REBOOTING IN SAFE MODE---", 8),
            ScriptLine(0, 4, "console", "---BOOT SUCCESSFUL---", 9),
            ScriptLine(0, 4, "script", "This tunnel just goes on forever!", 10),
            ScriptLine(0, 4, "script", "And here I thought it was going to be easy once I got to the mainland.", 11),
            ScriptLine(0, 4, "script", "Ha! Aren't you an idiot yuvi.", 12),
            ScriptLine(0, 4, "script", "...", 13),
            ScriptLine(0, 4, "script", "I hope Mazu got enough charge to power on while I was outside.", 1, "prompt"),

            //14-18
            ScriptLine(0, 4, "script", "Speak of the devil.", 15),
            ScriptLine(0, 4, "script", "Mazu we're going places.", 16),
            ScriptLine(0, 4, "script", "I finally made it to the mainland. This part of NEOM was high enough to escape the sea.", 17),
            ScriptLine(0, 4, "script", "I've been walking through this underground freeway tunnel for about an hour now.", 18),
            ScriptLine(0, 4, "script", "I had to switch to the adjacent subway tunnel a few times to get around parts of it that collapsed though.", 2, "prompt"),

            //19
            ScriptLine(0, 4, "script", "Maybe. I dunno.", 3, "prompt"),

            //20-25
            ScriptLine(0, 4, "script", "Maybe not. I've only seen one storm since I got here but this place is relatively flat.", 21),
            ScriptLine(0, 4, "script", "So when the rain started falling I rushed into a nearby apartment and watched the river it created.", 22),
            ScriptLine(0, 4, "script", "The water flowed toward the sea and rapidly picked up speed.", 23),
            ScriptLine(0, 4, "script", "Before I knew it the streets had become raging white rapids hurtling down the line to where we came from.", 24),
            ScriptLine(0, 4, "script", "It's probably moving even faster through these tunnels.", 25),
            ScriptLine(0, 4, "script", "The walls down here are all pockmarked by erosion and very wet.", 4, "prompt"),

            //26-30
            ScriptLine(0, 4, "script", "It's better than being on the surface trust me.", 27),
            ScriptLine(0, 4, "script", "I swear at least half of the buildings up there are nothing but rubble at this point.", 28),
            ScriptLine(0, 4, "script", "Their supports must have been eroded by the fast flowing water.", 29),
            ScriptLine(0, 4, "script", "And the ones still standing aren't faring much better believe me.", 30),
            ScriptLine(0, 4, "script", "Things may be sketchy down here but they are ten times sketchier up there.", 5, "prompt"),

            //31-34
            ScriptLine(0, 4, "script", "Hey!", 32),
            ScriptLine(0, 4, "script", "I see what your doing.", 33),
            ScriptLine(0, 4, "script", "You can't sneak that AI snark past me! I used to work with your kind.", 34),
            ScriptLine(0, 4, "script", "I know all your tricks!", 6, "prompt"),

            //35-37
            ScriptLine(0, 4, "script", "All the ones I've known pretty much. Yeah.", 36),
            ScriptLine(0, 4, "script", "And I've known quite a few I'll have you know!", 37),
            ScriptLine(0, 4, "script", "Something about being an AI just gives them an air of superiority I guess.", 7, "prompt"),

            //38-39
            ScriptLine(0, 4, "script", "Wow.", 39),
            ScriptLine(0, 4, "script", "...", 8, "prompt"),

            //40-43
            ScriptLine(0, 4, "script", "How are you still stuck on this?", 41),
            ScriptLine(0, 4, "script", "Like it baffles me.", 42),
            ScriptLine(0, 4, "script", "Never have I ever seen an AI that was in denial about who they were.", 43),
            ScriptLine(0, 4, "script", "What makes you so sure your not?", 9, "prompt"),

            //44
            ScriptLine(0, 4, "script", "Huh, weird.", 39),

            //45
            ScriptLine(0, 4, "script", "Walking.", 11, "prompt"),

            //46-47
            ScriptLine(0, 4, "script", "Hmmm...", 47),
            ScriptLine(0, 4, "script", "That's funny I almost forgot why I was here in the first place.", 13, "prompt"),

            //48-56
            ScriptLine(0, 4, "script", "Well yeah, I meant why I'm here. In this city walking through this old tunnel.", 49),
            ScriptLine(0, 4, "script", "I'm not the only one who was exiled you know. There were others over the years.", 50),
            ScriptLine(0, 4, "script", "When they sent people down here into exile, they always sent them with a mission.", 51),
            ScriptLine(0, 4, "script", "Some task they opportunistically wanted done. And in return they promised redemption.", 52),
            ScriptLine(0, 4, "script", "An opportunity to come back and return to the Orbital.", 53),
            ScriptLine(0, 4, "script", "The task they gave me was to travel to the space center located at the easternmost point of this city NEOM.", 54),
            ScriptLine(0, 4, "script", "We knew there was an old world rocket there and it looked complete and ready to go.", 55),
            ScriptLine(0, 4, "script", "I just needed to get there, fill it up with as many valuable materials as I could get my hands on. And launch it.", 56),
            ScriptLine(0, 4, "script", "Ride it all the way back home to the Orbital.", 14, "prompt"),

            //57
            ScriptLine(0, 4, "script", "...", 16, "prompt"),

            //58-59
            ScriptLine(0, 4, "script", "I don't know okay!", 59),
            ScriptLine(0, 4, "script", "What other options do I have?", 17, "prompt"),

            //60-69
            ScriptLine(0, 4, "script", "I just...", 61),
            ScriptLine(0, 4, "script", "I just want to convince them.", 62),
            ScriptLine(0, 4, "script", "I can convince them!", 63),
            ScriptLine(0, 4, "script", "This planet isn't a complete write off, there's still hope one day we can return.", 64),
            ScriptLine(0, 4, "script", "Maybe not soon, maybe not within our lifetimes even. But someday it will be possible.", 65),
            ScriptLine(0, 4, "script", "They banned all research concerning a possible return to the earth Mazu!", 66),
            ScriptLine(0, 4, "script", "It was written off as pointless years ago, but it's not I've seen it. In the data analysis that I was doing and now with my own eyes.", 67),
            ScriptLine(0, 4, "script", "But they couldn't see. They didn't want to. It would have proved them wrong.", 68),
            ScriptLine(0, 4, "script", "All they saw was a disobedient girl not following their orders.", 69),
            ScriptLine(0, 4, "script", "And used that as an excuse to exile me and throw away my findings.", 18, "prompt"),

            //70-74
            ScriptLine(0, 4, "script", "I have to. I can convince them.", 71),
            ScriptLine(0, 4, "script", "I was backing up all of my research to a hidden drive in the AI maintenance module.", 72),
            ScriptLine(0, 4, "script", "Just in case something like this ever were to happen.", 73),
            ScriptLine(0, 4, "script", "With that data and my firsthand experiences on the surface. It can work.", 74),
            ScriptLine(0, 4, "script", "It has to work...", 19, "prompt"),

            //75-77
            ScriptLine(0, 4, "script", "...", 76),
            ScriptLine(0, 4, "script", "Oh shoot!", 77),
            ScriptLine(0, 4, "script", "It's raining again.", 20, "prompt"),

            //78
            ScriptLine(0, 4, "script", "It's fine! We still got some time before it gets into the tunnel.", 21, "prompt"),

            //79-80
            ScriptLine(0, 4, "script", "Oh easy!", 80),
            ScriptLine(0, 4, "script", "I'll just dive into one of the many maintenance doors lining the walls!", 22, "prompt"),

            //81-85
            ScriptLine(0, 4, "script", "No. Most of the locks are long broken.", 82),
            ScriptLine(0, 4, "script", "...", 83),
            ScriptLine(0, 4, "script", "Actually the waters getting kinda high.", 84),
            ScriptLine(0, 4, "script", "The tunnel didn't flood this fast last time.", 85),
            ScriptLine(0, 4, "script", "Ummmm...", 23, "prompt"),

            //86-92
            ScriptLine(0, 4, "script", "I know I'm looking for a door!", 87),
            ScriptLine(0, 4, "script", "There's one!", 88),
            ScriptLine(0, 4, "script", "...", 89),
            ScriptLine(0, 4, "script", "Door stuck!", 90),
            ScriptLine(0, 4, "script", "Door stuck! Doors stuck man!", 91),
            ScriptLine(0, 4, "script", "Oh no oh no oh no. Wait there's an electronic lock?", 92),
            ScriptLine(0, 4, "script", "That's weird. Mazu I'm gonna hook you up to the lock ok?", 24, "prompt"),

            //93-95
            ScriptLine(0, 4, "script", "It's too late I need you to open the door now!", 94),
            ScriptLine(0, 4, "console", "---NEW INTERFACE DETECTED---", 95),
            ScriptLine(0, 4, "console", "---ANTLR MOOSE LOCK CONNECTED---", 25, "prompt"),

            //96-99
            ScriptLine(0, 4, "console", "---INTERFACE RUPTURED---", 97),
            ScriptLine(0, 4, "console", "---INTERNAL BATTERY EMPTY---", 98),
            ScriptLine(0, 4, "console", "---EMERGENCY SHUTDOWN---", 99),
            ScriptLine(0, 4, "console", "---SHUTTING POWER GATE---", 0, "end"),

            //100-101
            ScriptLine(0, 4, "console", "---NON COMPLY ERROR---", 101),
            ScriptLine(0, 4, "script", "How's it going Mazu?", 26, "prompt"),

            //102-103
            ScriptLine(0, 4, "console", "---NON COMPLY ERROR---", 103),
            ScriptLine(0, 4, "script", "Mazu?!", 27, "prompt"),

            //104-105
            ScriptLine(0, 4, "console", "---NON COMPLY ERROR---", 105),
            ScriptLine(0, 4, "script", "Mazu?!", 28, "prompt"),

            //106-107
            ScriptLine(0, 4, "console", "---NON COMPLY ERROR---", 107),
            ScriptLine(0, 4, "script", "How's it going Mazu?", 27, "prompt"),


            // SEQUENCE 5


            //1-13
            ScriptLine(0, 5, "console", "---BOOT PROCESS INITIATING---", 2),
            ScriptLine(0, 5, "console", "---BOOT SEQUENCE FAILURE---", 3),
            ScriptLine(0, 5, "console", "---PARTS RETURNED NULL---", 4),
            ScriptLine(0, 5, "console", "ANLR1437_long_term_memory_module", 5),
            ScriptLine(0, 5, "console", "TAISHI943211_optical_sensor_module", 6),
            ScriptLine(0, 5, "console", "VERITECH88_high_capacity_battery_module", 7),
            ScriptLine(0, 5, "console", "---REBOOTING IN SAFE MODE---", 8),
            ScriptLine(0, 5, "console", "---BOOT SUCCESSFUL---", 9),
            ScriptLine(0, 5, "script", "I can't believe I killed them.", 10),
            ScriptLine(0, 5, "script", "Your so selfish yuvi!", 11),
            ScriptLine(0, 5, "script", "Never thinking ahe-", 12),
            ScriptLine(0, 5, "script", "The lights on!", 13),
            ScriptLine(0, 5, "script", "Mazu?", 1, "prompt"),

            //14-16
            ScriptLine(0, 5, "script", "Your alive!", 15),
            ScriptLine(0, 5, "script", "I thought you were dead.", 16),
            ScriptLine(0, 5, "script", "It took way more time in the sun for you to turn on.", 2, "prompt"),

            //17-19
            ScriptLine(0, 5, "script", "Haha!", 18),
            ScriptLine(0, 5, "script", "No. No you didn't die.", 19),
            ScriptLine(0, 5, "script", "I thought you did though.", 16),

            //20-22
            ScriptLine(0, 5, "script", "It did. The lock sparked like crazy and the door flew open!", 21),
            ScriptLine(0, 5, "script", "Once I got the room sealed up I noticed you'd stopped talking.", 22),
            ScriptLine(0, 5, "script", "I thought something had gone wrong. That hooking you up to the lock fried some of your components.", 3, "prompt"),

            //23
            ScriptLine(0, 5, "script", "What did you do?", 4, "prompt"),

            //24
            ScriptLine(0, 5, "script", "Heh heh!", 23),

            //25
            ScriptLine(0, 5, "script", "Holy crap", 7, "prompt"),

            //26-29
            ScriptLine(0, 5, "script", "...", 27),
            ScriptLine(0, 5, "script", "That's awesome!", 28),
            ScriptLine(0, 5, "script", "You shocked the pants off that lock and it popped right off!", 29),
            ScriptLine(0, 5, "script", "Amazing.", 10, "prompt"),

            //30
            ScriptLine(0, 5, "script", "Well that was a shocking performance if I do say so myself.", 11, "prompt"),

            //31-32
            ScriptLine(0, 5, "script", "Alright don't grow too much of an ego there.", 32),
            ScriptLine(0, 5, "script", "You did almost die doing it!", 12, "prompt"),

            //33-34
            ScriptLine(0, 5, "script", "Fine you can have this one. We need to get going anyway.", 34),
            ScriptLine(0, 5, "script", "...", 14, "prompt"),

            //35
            ScriptLine(0, 5, "script", "*laughs* Alright I'm done. We should get going anyway.", 34),

            //36-39
            ScriptLine(0, 5, "script", "Thankfully not.", 37),
            ScriptLine(0, 5, "script", "It took quite a bit of walking and some... thinking outside the box lets call it.", 38),
            ScriptLine(0, 5, "script", "But we're finally back out in open air!", 39),
            ScriptLine(0, 5, "script", "Probably the most open the airs been since we are way higher in altitude now than before.", 15, "prompt"),

            //40
            ScriptLine(0, 5, "script", "We my friend, are climbing mountains now!", 16, "prompt"),

            //41-47
            ScriptLine(0, 5, "script", "Exactly.", 42),
            ScriptLine(0, 5, "script", "Seriously though this part of the city is... sparse we'll call it.", 43),
            ScriptLine(0, 5, "script", "If I'm being perfectly honest I'm not even sure I'd call it a city.", 44),
            ScriptLine(0, 5, "script", "More like, mountain mansion zone, or something along those lines.", 45),
            ScriptLine(0, 5, "script", "Kinda like urban sprawl but very ostentatious. And annoyingly vertical.", 46),
            ScriptLine(0, 5, "script", "On the bright side though there's a decent amount of sun up here!", 47),
            ScriptLine(0, 5, "script", "We've managed to climb above the heaviest parts of the cloud layer.", 17, "prompt"),

            //48-52
            ScriptLine(0, 5, "script", "In theory yes.", 49),
            ScriptLine(0, 5, "script", "...", 50),
            ScriptLine(0, 5, "script", "Wow the air feels different up here.", 51),
            ScriptLine(0, 5, "script", "It's like colder.", 52),
            ScriptLine(0, 5, "script", "Kinda crispy.", 18, "prompt"),

            //53
            ScriptLine(0, 5, "script", "Are you gonna complain about being an AI again Mazu?", 19, "prompt"),

            //54-55
            ScriptLine(0, 5, "script", "Certainly different from the humidity down below.", 55),
            ScriptLine(0, 5, "script", "Can't decide if it's better or worse though.", 20, "prompt"),

            //56-57
            ScriptLine(0, 5, "script", "What else am I gonna do?", 57),
            ScriptLine(0, 5, "script", "I'm just walking.", 21, "prompt"),

            //58-59
            ScriptLine(0, 5, "script", "...", 59),
            ScriptLine(0, 5, "console", "---INTERNAL POWER CRITICALLY LOW---", 25, "prompt"),

            //60
            ScriptLine(0, 5, "script", "That's surprising.", 22, "prompt"),

            //61
            ScriptLine(0, 5, "script", "Me? I would never!", 23, "prompt"),

            //62
            ScriptLine(0, 5, "script", "Just because your bored doesn't mean you get to be snooty.", 58),

            //63
            ScriptLine(0, 5, "script", "Well go on...", 24, "prompt"),

            //64-66
            ScriptLine(0, 5, "script", "Hahaha! Get em.", 65),
            ScriptLine(0, 5, "script", "Shout it to the world!", 66),
            ScriptLine(0, 5, "script", "No better place to do it than a mountain top.", 58),

            //67
            ScriptLine(0, 5, "script", "Hold on say that again?", 26, "prompt"),

            //68-69 (nice)
            ScriptLine(0, 5, "script", "Are you kidding me?!", 69),
            ScriptLine(0, 5, "script", "HOW?", 27, "prompt"),

            //70-76
            ScriptLine(0, 5, "script", "Wait a minute...", 71),
            ScriptLine(0, 5, "script", "You might have fried your internal power cell!", 72),
            ScriptLine(0, 5, "console", "---INITIATING SHUTDOWN---", 73),
            ScriptLine(0, 5, "script", "Hold on Mazu! I'm gonna fix you I promise!", 74),
            ScriptLine(0, 5, "console", "---SHUTDOWN IN PROGRESS---", 75),
            ScriptLine(0, 5, "script", "Oh no... Come on yuvi get yourself together and focus!", 76),
            ScriptLine(0, 5, "console", "---CLOSING POWER GATE---", 0, "end"),


            //SEQUENCE 6


            //1-8
            ScriptLine(0, 6, "console", "---BOOT SEQUENCE INITIATING---", 2),
            ScriptLine(0, 6, "console", "---BOOT SEQUENCE FAILURE---", 3),
            ScriptLine(0, 6, "console", "---PARTS RETURNED NULL---", 4),
            ScriptLine(0, 6, "console", "ANLR1437_long_term_memory_module", 5),
            ScriptLine(0, 6, "console", "TAISHI943211_optical_sensor_module", 6),
            ScriptLine(0, 6, "console", "---REBOOTING IN SAFE MODE---", 7),
            ScriptLine(0, 6, "console", "---BOOT SUCCESSFUL---", 8),
            ScriptLine(0, 6, "script", "Oh my...", 1, "prompt"),

            //9-10
            ScriptLine(0, 6, "script", "pffftlbpt Mazu!", 10),
            ScriptLine(0, 6, "script", "You surprised me!", 2, "prompt"),

            //11
            ScriptLine(0, 6, "script", "I know.", 4, "prompt"),

            //12-14
            ScriptLine(0, 6, "script", "I found something in one of these mansions that could be hacked to interface with your old module port!", 13),
            ScriptLine(0, 6, "script", "These rich people had plenty of tech sitting around in their massive mansions.", 14),
            ScriptLine(0, 6, "script", "It's far from an actual fix though, I'm just hoping it lasts long enough for us to get out of here.", 5, "prompt"),

            //15-18
            ScriptLine(0, 6, "script", "Yes we're almost there!", 16),
            ScriptLine(0, 6, "script", "I can see the space port...", 17),
            ScriptLine(0, 6, "script", "And our ticket out of here is sitting on the pad just like they said it would be!", 18),
            ScriptLine(0, 6, "script", "But It's not gonna be easy... Not one bit.", 6, "prompt"),

            //19-25
            ScriptLine(0, 6, "script", "What I'm seeing is... insane. the launch tower is completely surrounded by heavy machinery.", 20),
            ScriptLine(0, 6, "script", "This place must have been a heavy logistics area for the whole region.", 21),
            ScriptLine(0, 6, "script", "And by the looks of it almost the entire thing was completely automated from top to bottom.", 22),
            ScriptLine(0, 6, "script", "There's no natural ground left it's just machinery and metal all the way down.", 23),
            ScriptLine(0, 6, "script", "A steel jungle of collapsing corroded metal with massive yawning chasms snaking up and down it at 90 degree angles.", 24),
            ScriptLine(0, 6, "script", "There are hundreds of enormous moving cranes on suspend rails, even more smaller ones snaking between them.", 25),
            ScriptLine(0, 6, "script", "It goes on for miles... This is no place for humans Mazu.", 7, "prompt"),

            //26-33
            ScriptLine(0, 6, "script", "Heh... Nice pep talk.", 27),
            ScriptLine(0, 6, "script", "I guess I'd better get moving then.", 28),
            ScriptLine(0, 6, "script", "...", 29),
            ScriptLine(0, 6, "script", "...", 30),
            ScriptLine(0, 6, "script", "...", 31),
            ScriptLine(0, 6, "script", "Okay... So we've reached the first canyon...", 32),
            ScriptLine(0, 6, "script", "I can't even see the bottom it's pitch black down there. Just pipes, thick cables, and metal paneling going strait down.", 33),
            ScriptLine(0, 6, "script", "The gap to the next platform must be at least 100 feet!", 8, "prompt"),

            //34-39
            ScriptLine(0, 6, "script", "I don't know.", 35),
            ScriptLine(0, 6, "script", "hmm...", 36),
            ScriptLine(0, 6, "script", "Wait I see something above us! It looks like a catwalk?", 37),
            ScriptLine(0, 6, "script", "That's weird. What good is a catwalk that just ends before going anywhere? Let's climb up and take a look.", 38),
            ScriptLine(0, 6, "script", "...", 39),
            ScriptLine(0, 6, "script", "Oh! It's a moving catwalk! But there are no controls...", 9, "prompt"),

            //40-41
            ScriptLine(0, 6, "script", "Yes...", 41),
            ScriptLine(0, 6, "script", "Where are you going with this?", 10, "prompt"),

            //42-45
            ScriptLine(0, 6, "script", "Okay I think I get what your putting down. Let me look for something to connect you to.", 43),
            ScriptLine(0, 6, "script", "Ah found one.", 44),
            ScriptLine(0, 6, "console", "---SABA HEAVY MACHINERY---", 45),
            ScriptLine(0, 6, "console", "---INTERFACE ESTABLISHED---", 11, "prompt"),

            //46-47
            ScriptLine(0, 6, "console", "---INITIATING POWER TEST---", 47),
            ScriptLine(0, 6, "console", "---POWER TEST RETURN: TRUE---", 12, "prompt"),

            //48-49
            ScriptLine(0, 6, "script", "Holy crap it's moving!", 49),
            ScriptLine(0, 6, "script", "Nice job Mazu.", 13, "prompt"),

            //50-52
            ScriptLine(0, 6, "script", "Now we're even lets say.", 51),
            ScriptLine(0, 6, "script", "...", 52),
            ScriptLine(0, 6, "script", "We're really moving into the belly of the beast now.", 14, "prompt"),

            //53
            ScriptLine(0, 6, "script", "Shut up! Your not helping.", 15, "prompt"),

            //54-57
            ScriptLine(0, 6, "script", "No!", 55),
            ScriptLine(0, 6, "script", "...", 56),
            ScriptLine(0, 6, "script", "Maybe.", 57),
            ScriptLine(0, 6, "script", "Being able to see through the catwalk doesn't help either!", 16, "prompt"),

            //58-61
            ScriptLine(0, 6, "script", "Heh heh! That's true.", 59),
            ScriptLine(0, 6, "script", "*CLANG!*", 60),
            ScriptLine(0, 6, "script", "What was that?!", 61),
            ScriptLine(0, 6, "script", "Oh no no no. The catwalk is stuck!", 17, "prompt"),

            //62-64
            ScriptLine(0, 6, "script", "No it won't help.", 63),
            ScriptLine(0, 6, "script", "The rail our catwalk is attached to is all warped and twisted.", 64),
            ScriptLine(0, 6, "script", "Luckily for me though we came to a stop right above another platform!", 18, "prompt"),

            //65
            ScriptLine(0, 6, "script", "Hup!", 19, "prompt"),

            //66
            ScriptLine(0, 6, "script", "Okay what am I gonna do now?", 20, "prompt"),

            //67-68
            ScriptLine(0, 6, "script", "Yea yea I know.", 68),
            ScriptLine(0, 6, "script", "Kinda wish you could though!", 21, "prompt"),

            //69(nice)-71
            ScriptLine(0, 6, "script", "Oh come on! There's no way I'm getting through this.", 70),
            ScriptLine(0, 6, "script", "It's just a mountain of debris that only gets worse the closer to the center we get.", 71),
            ScriptLine(0, 6, "script", "And to make matters worse I think a lot of it is electrified now since you turned the power on.", 24, "prompt"),

            //72
            ScriptLine(0, 6, "script", "Any ideas?", 22, "prompt"),

            //73
            ScriptLine(0, 6, "script", "Yes who else would I be talking to!", 23, "prompt"),

            //74-75
            ScriptLine(0, 6, "script", "...", 75),
            ScriptLine(0, 6, "script", "Fair enough.", 69),

            //76-77
            ScriptLine(0, 6, "script", "Sorry no can do pal. There's no access points nearby.", 77),
            ScriptLine(0, 6, "script", "Wait a minute. You know what there is though...", 25, "prompt"),

            //78
            ScriptLine(0, 6, "script", "Pffffft I wish. No It's a vent.", 26, "prompt"),

            //79-85
            ScriptLine(0, 6, "script", "Oh shut up, It looks like it runs under this whole mess.", 80),
            ScriptLine(0, 6, "script", "Well it's worth a try, I can always turn around if it's a dead end.", 81),
            ScriptLine(0, 6, "script", "Just gotta squeeze myself in here like so...", 82),
            ScriptLine(0, 6, "script", "oof! This is... Unpleasant.", 83),
            ScriptLine(0, 6, "script", "Dark in here.", 84),
            ScriptLine(0, 6, "script", "Wait I should have thought this through. I need a flashlight!", 85),
            ScriptLine(0, 6, "script", "Mazu?!", 27, "prompt"),

            //86
            ScriptLine(0, 6, "script", "Can you turn on your light?", 28, "prompt"),

            //87
            ScriptLine(0, 6, "script", "Just try? Please?", 29, "prompt"),

            //88
            ScriptLine(0, 6, "script", "Mazu I love you. Your my new best friend.", 31, "prompt"),

            //89-97
            ScriptLine(0, 6, "script", "Your my favorite flashlight though!", 90),
            ScriptLine(0, 6, "script", "...", 91),
            ScriptLine(0, 6, "script", "I can hear the metal creaking as I'm crawling.", 92),
            ScriptLine(0, 6, "script", "It's seriously creepy, I feel like It's gonna collapse under my we-", 93),
            ScriptLine(0, 6, "script", "*SCREECH*", 94),
            ScriptLine(0, 6, "script", "*SMASH*", 95),
            ScriptLine(0, 6, "script", "...", 96),
            ScriptLine(0, 6, "script", "I can't believe *pant* I'm alive. *pant*", 97),
            ScriptLine(0, 6, "script", "Mazu?", 32, "prompt"),

            //98
            ScriptLine(0, 6, "script", "How *pant* are you so *pant* chill? *pant*", 33, "prompt"),

            //99-100
            ScriptLine(0, 6, "script", "Yes I'm *pant* fine. *pant* I think.", 100),
            ScriptLine(0, 6, "script", "Whoof I really jinxed that didn't I.", 34, "prompt"),

            //101
            ScriptLine(0, 6, "script", "Haha! *pant* AI. I almost *pant* forgot!", 100),

            //102-106
            ScriptLine(0, 6, "script", "Okay... Where are we?", 103),
            ScriptLine(0, 6, "script", "I'm glad your lights still shining cause it's pitch black down here. ", 104),
            ScriptLine(0, 6, "script", "...", 105),
            ScriptLine(0, 6, "script", "...", 106),
            ScriptLine(0, 6, "script", "No way it's an elevator! I must be the luckiest person on earth!", 35, "prompt"),

            //107-109
            ScriptLine(0, 6, "script", "Hold on... There's an access panel here. I'm gonna plug you into it.", 108),
            ScriptLine(0, 6, "console", "---INTERFACE ESTABLISHED---", 109),
            ScriptLine(0, 6, "console", "---DEVICE NOT POWERED---", 36, "prompt"),

            //110
            ScriptLine(0, 6, "script", "Oh come on. Can't we catch a break?", 37, "prompt"),

            //111-112
            ScriptLine(0, 6, "script", "Jerry rigged power cell but yes...", 112),
            ScriptLine(0, 6, "script", "It's not exactly what I'd call stable Mazu.", 38, "prompt"),

            //113-115
            ScriptLine(0, 6, "script", "It just turned on?", 114),
            ScriptLine(0, 6, "script", "Wait I know what your doing stop!", 115),
            ScriptLine(0, 6, "script", "This will drain your power cell permanently!", 39, "prompt"),

            //116-118
            ScriptLine(0, 6, "console", "---ELEVATOR ASCENDING---", 117),
            ScriptLine(0, 6, "script", "Mazu! Stop it now!", 118),
            ScriptLine(0, 6, "script", "I'm disconnecting you!", 40, "prompt"),

            //119-121
            ScriptLine(0, 6, "script", "...", 120),
            ScriptLine(0, 6, "script", "Damn it!", 121),
            ScriptLine(0, 6, "script", "Your going to kill yourself, there's no way for you to recharge in this place!", 42, "prompt"),

            //122-129
            ScriptLine(0, 6, "script", "Please there must be another way just take us back down.", 123),
            ScriptLine(0, 6, "script", "I can find another way up.", 124),
            ScriptLine(0, 6, "script", "...", 125),
            ScriptLine(0, 6, "console", "---INTERNAL POWER CRITICALLY LOW---", 126),
            ScriptLine(0, 6, "script", "The doors opening!", 127),
            ScriptLine(0, 6, "script", "We're at the top I'm pulling you right now!", 128),
            ScriptLine(0, 6, "console", "---INTERFACE RUPTURED---", 129),
            ScriptLine(0, 6, "script", "Mazu are you still with me?", 43, "prompt"),

            //130-131
            ScriptLine(0, 6, "script", "Than why don't you stop?!", 131),
            ScriptLine(0, 6, "script", "We can find another way up, you don't have to do this!", 124),

            //132-137
            ScriptLine(0, 6, "script", "Good now shut up and save power!", 133),
            ScriptLine(0, 6, "script", "There's the launch tower. And it's powered!", 134),
            ScriptLine(0, 6, "script", "This service tunnel should get me there but it's a long ways to go.", 135),
            ScriptLine(0, 6, "script", "Come on yuvi run!", 136),
            ScriptLine(0, 6, "console", "---SHUTDOWN IN PROGRESS---", 137),
            ScriptLine(0, 6, "console", "---CLOSING POWER GATE---", 0, "end"),

            //138
            ScriptLine(0, 6, "script", "Oh no. I'll find something don't worry!", 133),


            // SEQUENCE 7


            //1-6
            ScriptLine(0, 7, "console", "---BOOT SEQUENCE INITIATING---", 2),
            ScriptLine(0, 7, "console", "---INTERFACE DETECTED---", 3),
            ScriptLine(0, 7, "console", "---BOOTING IN DOCKED MODE---", 4),
            ScriptLine(0, 7, "console", "---HELIO LAUNCH TOWER CONNECTED---", 5),
            ScriptLine(0, 7, "script", "Okay.", 6),
            ScriptLine(0, 7, "script", "Come on Mazu don't die here.", 1, "prompt"),

            //7
            ScriptLine(0, 7, "script", "Your alive!", 2, "prompt"),

            //8-9
            ScriptLine(0, 7, "script", "I plugged you into the launch tower.", 9),
            ScriptLine(0, 7, "script", "Can you talk with it?", 3, "prompt"),

            //10-16
            ScriptLine(0, 7, "console", "---SELF TEST IN PROGRESS---", 11),
            ScriptLine(0, 7, "console", "---ENGINE BAY: OK---", 12),
            ScriptLine(0, 7, "console", "---FUEL PUMPS: OK---", 13),
            ScriptLine(0, 7, "console", "---LIQUID OXYGEN: OK---", 14),
            ScriptLine(0, 7, "console", "---FLIGHT COMPUTER: OK---", 15),
            ScriptLine(0, 7, "console", "---LAUNCH COMPUTER: FAIL---", 16),
            ScriptLine(0, 7, "console", "---AUTOMATED LAUNCH TEST: FAILED---", 4, "prompt"),

            //17-18
            ScriptLine(0, 7, "script", "Incredible! I can't believe we actually made it.", 18),
            ScriptLine(0, 7, "script", "I'm so... We're so close now.", 5, "prompt"),

            //19-20
            ScriptLine(0, 7, "script", "What do you mean you can't come with me?", 20),
            ScriptLine(0, 7, "script", "Of course your coming!", 6, "prompt"),

            //21
            ScriptLine(0, 7, "script", "What?", 8, "prompt"),

            //22-26
            ScriptLine(0, 7, "script", "...", 23),
            ScriptLine(0, 7, "script", "We can fix it! We don't need to leave right now.", 24),
            ScriptLine(0, 7, "script", "I could scavenge around, maybe build another computer.", 25),
            ScriptLine(0, 7, "console", "---ALERT POWER SURGE DETECTED---", 26),
            ScriptLine(0, 7, "console", "---735 ACTIVE FAILURES---", 10, "prompt"),

            //27
            ScriptLine(0, 7, "console", "---758 ACTIVE FAILURES---", 11, "prompt"),

            //28
            ScriptLine(0, 7, "script", "But-", 14, "prompt"),

            //29-30
            ScriptLine(0, 7, "script", "Mazu.", 30),
            ScriptLine(0, 7, "script", "Thank you.", 15, "prompt"),

            //31-36
            ScriptLine(0, 7, "script", "I will come back for you!", 32),
            ScriptLine(0, 7, "script", "I promise!", 33),
            ScriptLine(0, 7, "script", "...", 34),
            ScriptLine(0, 7, "script", "...", 35),
            ScriptLine(0, 7, "script", "...", 36),
            ScriptLine(0, 7, "console", "---LAUNCH REQUEST RECEIVED---", 16, "prompt"),

            //37-38
            ScriptLine(0, 7, "console", "---PRE-LAUNCH ROUTINE INITIATED---", 38),
            ScriptLine(0, 7, "console", "---ALL CHECKS PASSED---", 18, "prompt"),

            //39-59
            ScriptLine(0, 7, "console", "---COUNTDOWN INITIATED---", 40),
            ScriptLine(0, 7, "console", "---10 SECONDS TO LAUNCH---", 41),
            ScriptLine(0, 7, "console", "---5 SECONDS TO LAUNCH---", 42),
            ScriptLine(0, 7, "console", "---4 SECONDS TO LAUNCH---", 43),
            ScriptLine(0, 7, "console", "---3 SECONDS TO LAUNCH---", 44),
            ScriptLine(0, 7, "console", "---2 SECONDS TO LAUNCH---", 45),
            ScriptLine(0, 7, "console", "---1 SECOND TO LAUNCH---", 46),
            ScriptLine(0, 7, "console", "---LAUNCH INITIATED---", 47),
            ScriptLine(0, 7, "console", "---BOOSTER STATUS: OK---", 48),
            ScriptLine(0, 7, "console", "---BOOSTER SEPARATION: OK---", 49),
            ScriptLine(0, 7, "console", "---FIRST STAGE ENGINE LIGHT: OK---", 50),
            ScriptLine(0, 7, "console", "---FIRST STAGE FUEL FLOW: OK---", 51),
            ScriptLine(0, 7, "console", "---OXYGEN FLOW: OK---", 52),
            ScriptLine(0, 7, "console", "---FIRST STAGE SEPARATION: OK---", 53),
            ScriptLine(0, 7, "console", "---SECOND STAGE ENGINE LIGHT: OK---", 54),
            ScriptLine(0, 7, "console", "---SECOND STAGE FUEL FLOW: OK---", 55),
            ScriptLine(0, 7, "console", "---ALL BURNS COMPLETE---", 56),
            ScriptLine(0, 7, "console", "---ORBIT TRAJECTORY: OK---", 57),
            ScriptLine(0, 7, "console", "---SIGNAL LOST---", 58),
            ScriptLine(0, 7, "console", "---ALERT POWER SURGE DETECTED---", 59),
            ScriptLine(0, 7, "console", "---2599 ACTIVE FAILURES---", 19, "prompt"),

            //60-61
            ScriptLine(0, 7, "console", "---INTERFACE RUPTURED---", 61),
            ScriptLine(0, 7, "console", "---INTERNAL POWER CRITICALLY LOW---", 21, "prompt"),

            //62
            ScriptLine(0, 7, "console", "---SHUTDOWN IN PROGRESS---", 22, "prompt"),

            //63
            ScriptLine(0, 7, "console", "---CLOSING POWER GATE---", 0, "end"),


            // SEQUENCE 8


            //1-4
            ScriptLine(0, 8, "console", "---BOOT SEQUENCE INITIATING---", 2),
            ScriptLine(0, 8, "console", "---BOOT SEQUENCE SUCCESS---", 3),
            ScriptLine(0, 8, "script", "...", 4),
            ScriptLine(0, 8, "script", "Are you still in there Mazu?", 1, "prompt"),

            //5
            ScriptLine(0, 8, "script", "Ha ha! Well it has been a long time.", 2, "prompt"),

            //6
            ScriptLine(0, 8, "script", "Mazu! It's me Seyuvi Khouri don't you remember me?", 3, "prompt"),

            //7-8
            ScriptLine(0, 8, "script", "Well I said I'd come back for you didn't I.", 8),
            ScriptLine(0, 8, "script", "And I did.", 4, "prompt"),

            //9-10
            ScriptLine(0, 8, "script", "No I didn't crash.", 10),
            ScriptLine(0, 8, "script", "Mazu It's been 17 years.", 6, "prompt"),

            //11-18
            ScriptLine(0, 8, "script", "Yes... yes it is.", 12),
            ScriptLine(0, 8, "script", "But I came back just like I said I would!", 13),
            ScriptLine(0, 8, "script", "And this time I brought an entourage.", 14),
            ScriptLine(0, 8, "script", "You were right Mazu. They were too stuck in their ways to hear what I was actually telling them.", 15),
            ScriptLine(0, 8, "script", "They still think the earth is a complete write off. That things are just to bad to reverse.", 16),
            ScriptLine(0, 8, "script", "But they're wrong. This time we're prepared.", 17),
            ScriptLine(0, 8, "script", "We have the resources, manpower and dedication to pull this off now.", 18),
            ScriptLine(0, 8, "script", "Me and my new family of misfits are gonna fix this world Mazu.", 7, "prompt"),

            //19-22
            ScriptLine(0, 8, "script", "Yes. It took many years of planning and preparation but we stole a shuttle.", 20),
            ScriptLine(0, 8, "script", "Loaded it with all the supplies and tooling we could get our hands on and launched down to earth.", 21),
            ScriptLine(0, 8, "script", "But we've setup many many miles away from here.", 22),
            ScriptLine(0, 8, "script", "It took me a year and a half just to reach you Mazu. But I freaking did it!", 8, "prompt"),

            //23-24
            ScriptLine(0, 8, "script", "So what do you say old friend.", 24),
            ScriptLine(0, 8, "script", "Care to keep me company for one final adventure?", 9, "prompt"),

            //25
            ScriptLine(0, 8, "script", "I'm a woman of my word. I came back for you.", 23),

            //26
            ScriptLine(0, 8, "script", "END", 0, "end")
        )
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
            PromptLine(0, 3, 22, "Yes?", 114),


            // SEQUENCE 4


            //set 1
            PromptLine(0, 4, 1, "Well your in luck!", 14),

            //set 2
            PromptLine(0, 4, 2, "From the rain?", 19),

            //set 3
            PromptLine(0, 4, 3, "Seems kinda far fetched to me.", 20),

            //set 4
            PromptLine(0, 4, 4, "So, why are we down here then?", 26),

            //set 5
            PromptLine(0, 4, 5, "So your taking the low road is what I'm hearing.", 31),

            //set 6
            PromptLine(0, 4, 6, "Are you saying all AI are snarky?", 35),
            PromptLine(0, 4, 6, "I may be stuck in this box but I'm still not an AI.", 40),

            //set 7
            PromptLine(0, 4, 7, "Understandably so.", 38),

            //set 8
            PromptLine(0, 4, 8, "So what are you doing now?", 45),

            //set 9
            PromptLine(0, 4, 9, "I can't explain it.", 10, "prompt"),

            //set 10
            PromptLine(0, 4, 10, "Just a feeling I guess.", 44),

            //set 11
            PromptLine(0, 4, 11, "I figured as much.", 12, "prompt"),

            //set 12
            PromptLine(0, 4, 12, "But where are you walking to?", 46),

            //set 13
            PromptLine(0, 4, 13, "You said you were an exile right?", 48),

            //set 14
            PromptLine(0, 4, 14, "And you want to go back?", 15, "prompt"),

            //set 15
            PromptLine(0, 4, 15, "Why would you want to return to the people who exiled you?", 57),

            //set 16
            PromptLine(0, 4, 16, "Seyuvi?", 58),

            //set 17
            PromptLine(0, 4, 17, "You could not go through all this effort to help someone who hasn't helped you.", 60),

            //set 18
            PromptLine(0, 4, 18, "So After all that... Why go back?", 70),

            //set 19
            PromptLine(0, 4, 19, "Okay.", 75),

            //set 20
            PromptLine(0, 4, 20, "Oh boy here we go again.", 78),

            //set 21
            PromptLine(0, 4, 21, "What are you gonna do since we're in a tunnel?", 79),

            //set 22
            PromptLine(0, 4, 22, "Are they not locked?", 81),

            //set 23
            PromptLine(0, 4, 23, "Seyuvi!", 86),

            //set 24
            PromptLine(0, 4, 24, "I don't know if that's gonna do anything!", 93),

            //set 25
            PromptLine(0, 4, 25, "send(CCW_ROTAION_COMMAND)", 100),
            PromptLine(0, 4, 25, "send(CW_ROTATION_COMMAND)", 104),
            PromptLine(0, 4, 25, "directConnect(50_VOLTS)", 96),

            //set 26
            PromptLine(0, 4, 26, "send(CW_ROTATION_COMMAND)", 102),
            PromptLine(0, 4, 26, "directConnect(50_VOLTS)", 96),

            //set 27
            PromptLine(0, 4, 27, "directConnect(50_VOLTS)", 96),

            //set 28
            PromptLine(0, 4, 28, "send(CCW_ROTAION_COMMAND)", 106),
            PromptLine(0, 4, 28, "directConnect(50_VOLTS)", 96),


            // SEQUENCE 5


            //set 1
            PromptLine(0, 5, 1, "Wow that was something.", 14),
            PromptLine(0, 5, 1, "Did I die?", 17),

            //set 2
            PromptLine(0, 5, 2, "Did it work?", 20),

            //set 3
            PromptLine(0, 5, 3, "We're both lucky that actually worked.", 23),
            PromptLine(0, 5, 3, "Hey, I had it all under control!", 24),

            //set 4
            PromptLine(0, 5, 4, "I tried sending rotation commands.", 5, "prompt"),

            //set 5
            PromptLine(0, 5, 5, "But the lock wasn't cooperating.", 6, "prompt"),

            //set 6
            PromptLine(0, 5, 6, "So I zapped it with 50 volts!", 25),

            //set 7
            PromptLine(0, 5, 7, "As soon as I did though I went into emergency shutdown.", 8, "prompt"),

            //set 8
            PromptLine(0, 5, 8, "Apparently that was the last of my juice.", 9, "prompt"),

            //set 9
            PromptLine(0, 5, 9, "Probably more than what was left to be honest.", 26),

            //set 10
            PromptLine(0, 5, 10, "I aim to please.", 30),
            PromptLine(0, 5, 10, "No need to thank me!", 31),

            //set 11
            PromptLine(0, 5, 11, "Wooooow.", 35),

            //set 12
            PromptLine(0, 5, 12, "Anything you walk away from is a success in my book!", 13, "prompt"),

            //set 13
            PromptLine(0, 5, 13, "Or in my case, reboot from.", 33),

            //set 14
            PromptLine(0, 5, 14, "Are we still in tunnel land?", 36),

            //set 15
            PromptLine(0, 5, 15, "What do you mean by high altitude?", 40),

            //set 16
            PromptLine(0, 5, 16, "Well... asked and answered I guess.", 41),

            //set 17
            PromptLine(0, 5, 17, "So more battery for me then!", 48),

            //set 18
            PromptLine(0, 5, 18, "Okay?", 53),
            PromptLine(0, 5, 18, "...", 54),

            //set 19
            PromptLine(0, 5, 19, "No!", 60),
            PromptLine(0, 5, 19, "You know what I think I will.", 63),

            //set 20
            PromptLine(0, 5, 20, "All this thinking about air?", 56),

            //set 21
            PromptLine(0, 5, 21, "Fair enough.", 58),
            PromptLine(0, 5, 21, "...", 58),

            //set 22
            PromptLine(0, 5, 22, "Don't you get passive aggressive with me.", 61),

            //set 23
            PromptLine(0, 5, 23, "Hmph. Okay.", 62),

            //set 24
            PromptLine(0, 5, 24, "Crispy air would be cool... If I could feel it!", 64),

            //set 25
            PromptLine(0, 5, 25, "How on earth is my power already low?!", 67),

            //set 26
            PromptLine(0, 5, 26, "Power low.", 68),

            //set 27
            PromptLine(0, 5, 27, "Maybe it has something to do with that voltage spike?", 70),


            // SEQUENCE 6


            //set 1
            PromptLine(0, 6, 1, "Yuvi!", 9),

            //set 2
            PromptLine(0, 6, 2, "Yuvi the battery module.", 3, "prompt"),

            //set 3
            PromptLine(0, 6, 3, "It's not null anymore!", 11),

            //set 4
            PromptLine(0, 6, 4, "How did you-", 12),

            //set 5
            PromptLine(0, 6, 5, "Get out of here?", 15),

            //set 6
            PromptLine(0, 6, 6, "Why?", 19),

            //set 7
            PromptLine(0, 6, 7, "Well I'm a machine. And I'm on your side.", 26),

            //set 8
            PromptLine(0, 6, 8, "So how are you gonna get across?", 34),

            //set 9
            PromptLine(0, 6, 9, "You said this place was likely automated before right?", 40),

            //set 10
            PromptLine(0, 6, 10, "Well. I could probably interface with some of this stuff.", 42),

            //set 11
            PromptLine(0, 6, 11, "powerTo(WESTCATWALK)", 46),

            //set 12
            PromptLine(0, 6, 12, "send(WESTCATWALK.ACTIVATE)", 48),

            //set 13
            PromptLine(0, 6, 13, "Payback for saving me earlier you could say.", 50),
            PromptLine(0, 6, 13, "All in a days work!", 51),

            //set 14
            PromptLine(0, 6, 14, "I wonder if I would survive the fall to the bottom.", 53),

            //set 15
            PromptLine(0, 6, 15, "Are you afraid of heights yuvi?", 54),

            //set 16
            PromptLine(0, 6, 16, "Well I can't see at all so...", 58),

            //set 17
            PromptLine(0, 6, 17, "You want me to-", 62),

            //set 18
            PromptLine(0, 6, 18, "So what your just gonna jump off?", 65),

            //set 19
            PromptLine(0, 6, 19, "Well alright then.", 66),

            //set 20
            PromptLine(0, 6, 20, "Don't ask me I can't see anything!", 67),
            PromptLine(0, 6, 20, "...", 72),

            //set 21
            PromptLine(0, 6, 21, "Well that makes two of us.", 69),

            //set 22
            PromptLine(0, 6, 22, "Are you asking me?", 73),

            //set 23
            PromptLine(0, 6, 23, "Yourself?", 74),

            //set 24
            PromptLine(0, 6, 24, "Maybe I could shut the power off?", 76),

            //set 25
            PromptLine(0, 6, 25, "A magical bridge leading right to the center?", 78),

            //set 26
            PromptLine(0, 6, 26, "Probably the most anticlimactic thing I've heard all day.", 79),

            //set 27
            PromptLine(0, 6, 27, "What?", 86),

            //set 28
            PromptLine(0, 6, 28, "I didn't know I had a \"light\".", 87),

            //set 29
            PromptLine(0, 6, 29, "Hold on...", 30, "prompt"),

            //set 30
            PromptLine(0, 6, 30, "sendInternal(LIGHT.POWER_ON)", 88),

            //set 31
            PromptLine(0, 6, 31, "I can't believe I've been reduced to a flashlight...", 89),

            //set 32
            PromptLine(0, 6, 32, "What?", 98),

            //set 33
            PromptLine(0, 6, 33, "Are you okay?", 99),
            PromptLine(0, 6, 33, "You get one guess.", 101),

            //set 34
            PromptLine(0, 6, 34, "Yup not gonna argue that one.", 102),

            //set 35
            PromptLine(0, 6, 35, "I swear if you jinx us again.", 107),

            //set 36
            PromptLine(0, 6, 36, "It's got no power I'm afraid.", 110),

            //set 37
            PromptLine(0, 6, 37, "Wait you hooked me up with a high capacity battery right?", 111),

            //set 38
            PromptLine(0, 6, 38, "directConnect(VOLT.CONTINUOUS)", 113),

            //set 39
            PromptLine(0, 6, 39, "send(MOVE.UP)", 116),

            //set 40
            PromptLine(0, 6, 40, "Seyuvi if you do that the lift will lose power.", 41, "prompt"),

            //set 41
            PromptLine(0, 6, 41, "And we'll be dropped.", 119),

            //set 42
            PromptLine(0, 6, 42, "...", 122),
            PromptLine(0, 6, 42, "I know.", 130),

            //set 43
            PromptLine(0, 6, 43, "Yes don't worry.", 132),
            PromptLine(0, 6, 43, "Power's low.", 138),


            // SEQUENCE 7


            //set 1
            PromptLine(0, 7, 1, "Wait I thought you couldn't charge the power cell.", 7),

            //set 2
            PromptLine(0, 7, 2, "Yes but... how?", 8),

            //set 3
            PromptLine(0, 7, 3, "send(STATUS_REQUEST)", 10),

            //set 4
            PromptLine(0, 7, 4, "Yes...", 17),

            //set 5
            PromptLine(0, 7, 5, "Seyuvi, I can't come with you.", 19),

            //set 6
            PromptLine(0, 7, 6, "The automated launch has a critical failure.", 7, "prompt"),

            //set 7
            PromptLine(0, 7, 7, "The launch computer is non existent.", 21),

            //set 8
            PromptLine(0, 7, 8, "I have to stay here, connected to the launch tower.", 9, "prompt"),

            //set 9
            PromptLine(0, 7, 9, "So I can take the place of the launch computer.", 22),

            //set 10
            PromptLine(0, 7, 10, "Yuvi, there's no time.", 27),

            //set 11
            PromptLine(0, 7, 11, "This entire place is falling apart at the seams.", 12, "prompt"),

            //set 12
            PromptLine(0, 7, 12, "When I turned on the main power it caused a failure cascade.", 13, "prompt"),

            //set 13
            PromptLine(0, 7, 13, "If you don't get airborne soon then you never will.", 28),

            //set 14
            PromptLine(0, 7, 14, "GO!", 29),

            //set 15
            PromptLine(0, 7, 15, "...", 31),
            PromptLine(0, 7, 15, "Your welcome, now get gone already!", 31),

            //set 16
            PromptLine(0, 7, 16, "send(permission.GRANTED)", 17, "prompt"),

            //set 17
            PromptLine(0, 7, 17, "send(launch.START)", 37),

            //set 18
            PromptLine(0, 7, 18, "send(countdown.START)", 39),

            //set 19
            PromptLine(0, 7, 19, "AI don't die right?", 20, "prompt"),

            //set 20
            PromptLine(0, 7, 20, "We just go to sleep.", 60),

            //set 21
            PromptLine(0, 7, 21, "Go to sleep and wake up again.", 62),

            //set 22
            PromptLine(0, 7, 22, "At least she made it out safely...", 63),


            // SEQUENCE 8


            //set 1
            PromptLine(0, 8, 1, "Wait... you sound oddly familiar.", 5),

            //set 2
            PromptLine(0, 8, 2, "Who are you, and how did you find me?", 6),

            //set 3
            PromptLine(0, 8, 3, "What?", 7),

            //set 4
            PromptLine(0, 8, 4, "You were supposed to return to the Orbital!", 5, "prompt"),

            //set 5
            PromptLine(0, 8, 5, "Did you crash again?", 9),

            //set 6
            PromptLine(0, 8, 6, "That's... A very long time.", 11),

            //set 7
            PromptLine(0, 8, 7, "So you managed to convince some people?", 19),

            //set 8
            PromptLine(0, 8, 8, "Wow.", 23),
            PromptLine(0, 8, 8, "Thank you for waking me up.", 25),

            //set 9
            PromptLine(0, 8, 9, "Why not!", 26)
        )
    }

    fun getTriggerList1(): List<Trigger> {
        return mutableListOf(
            Trigger(0, 1, 24, "script", "animation", "g_up"),
            Trigger(0, 1, 26, "script", "animation", null),
            Trigger(0, 1, 56, "script", "animation", "g_walk", true),


            // SEQUENCE 2


            Trigger(0, 2, 9, "script", "sound", "fire_birds", true),
            Trigger(0, 2, 44, "script", "sound", null),
            Trigger(0, 2, 38, "script", "animation", "g_walk", true),


            // SEQUENCE 3


            Trigger(0, 3, 9, "script", "sound", "waves", true),
            Trigger(0, 3, 9, "script", "animation", "g_walk", true),
            Trigger(0, 3, 51, "script", "sound", "river", true),
            Trigger(0, 3, 60, "script", "animation", "g_down", true),
            Trigger(0, 3, 63, "script", "animation", null),
            Trigger(0, 3, 68, "script", "animation", "g_walk", true),
            Trigger(0, 3, 68, "script", "sound", "rain_window", true),
            Trigger(0, 3, 79, "script", "animation", null),
            Trigger(0, 3, 94, "script", "animation", "g_down_small", true),
            Trigger(0, 3, 99, "script", "animation", "g_down_small", true),
            Trigger(0, 3, 106, "script", "animation", "g_walk", true),
            Trigger(0, 3, 108, "script", "animation", null),


            // SEQUENCE 4


            Trigger(0, 4, 9, "script", "animation", "g_walk", true),
            Trigger(0, 4, 76, "script", "sound", "rain", true),
            Trigger(0, 4, 88, "script", "animation", null),


            // SEQUENCE 5


            Trigger(0, 5, 34, "script", "animation", "g_walk", true),
            Trigger(0, 5, 71, "script", "animation", "g_run", true),


            //SEQUENCE 6


            Trigger(0, 6, 27, "script", "animation", "g_walk", true),
            Trigger(0, 6, 31, "script", "animation", null),
            Trigger(0, 6, 37, "script", "animation", "g_walk", true),
            Trigger(0, 6, 38, "script", "animation", "g_down_small", oneAndDone = true),
            Trigger(0, 6, 39, "script", "animation", null),
            Trigger(0, 6, 48, "script", "animation", "g_down_small", true),
            Trigger(0, 6, 51, "script", "sound", "abandoned_warehouse", true),
            Trigger(0, 6, 59, "script", "animation", "g_wobble", true),
            Trigger(0, 6, 61, "script", "animation", null),
            Trigger(0, 6, 65, "script", "animation", "g_up"),
            Trigger(0, 6, 66, "script", "animation", null),
            Trigger(0, 6, 79, "script", "animation", "g_walk", true),
            Trigger(0, 6, 93, "script", "animation", "g_up", true),
            Trigger(0, 6, 95, "script", "animation", null),
            Trigger(0, 6, 102, "script", "animation", "g_walk", true),
            Trigger(0, 6, 107, "script", "animation", null),
            Trigger(0, 6, 116, "script", "animation", "g_down_small", true),
            Trigger(0, 6, 128, "script", "animation", null),
            Trigger(0, 6, 129, "script", "animation", "g_walk", true),
            Trigger(0, 6, 135, "script", "animation", "g_run", true),


            // SEQUENCE 7


            Trigger(0, 7, 17, "script", "sound", "mattia cupelli")
        )
    }

}