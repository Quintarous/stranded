package com.example.stranded.database

import com.example.stranded.R


fun getScriptLineList7(): List<ScriptLine> {
    return listOf(
        //1-6
        ScriptLine(0, 7, "console", "--- BOOT SEQUENCE INITIATING ---", 2),
        ScriptLine(0, 7, "console", "--- INTERFACE DETECTED ---", 3),
        ScriptLine(0, 7, "console", "--- BOOTING IN DOCKED MODE ---", 4),
        ScriptLine(0, 7, "console", "--- HELIO LAUNCH TOWER CONNECTED ---", 5),
        ScriptLine(0, 7, "script", "Okay.", 6),
        ScriptLine(0, 7, "script", "Come on Mazu don't die here.", 1, "prompt"),

        //7
        ScriptLine(0, 7, "script", "Your alive!", 2, "prompt"),

        //8-9
        ScriptLine(0, 7, "script", "I plugged you into the launch tower.", 9),
        ScriptLine(0, 7, "script", "Can you talk with it?", 3, "prompt"),

        //10-16
        ScriptLine(0, 7, "console", "--- SELF TEST IN PROGRESS ---", 11),
        ScriptLine(0, 7, "console", "--- ENGINE BAY: OK ---", 12),
        ScriptLine(0, 7, "console", "--- FUEL PUMPS: OK ---", 13),
        ScriptLine(0, 7, "console", "--- LIQUID OXYGEN: OK ---", 14),
        ScriptLine(0, 7, "console", "--- FLIGHT COMPUTER: OK ---", 15),
        ScriptLine(0, 7, "console", "--- LAUNCH COMPUTER: FAIL ---", 16),
        ScriptLine(0, 7, "console", "--- AUTOMATED LAUNCH TEST: FAILED ---", 4, "prompt"),

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
        ScriptLine(0, 7, "console", "--- ALERT POWER SURGE DETECTED ---", 26),
        ScriptLine(0, 7, "console", "--- 735 ACTIVE FAILURES ---", 10, "prompt"),

        //27
        ScriptLine(0, 7, "console", "--- 758 ACTIVE FAILURES ---", 11, "prompt"),

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
        ScriptLine(0, 7, "console", "--- LAUNCH REQUEST RECEIVED ---", 16, "prompt"),

        //37-38
        ScriptLine(0, 7, "console", "--- PRE-LAUNCH ROUTINE INITIATED ---", 38),
        ScriptLine(0, 7, "console", "--- ALL CHECKS PASSED ---", 18, "prompt"),

        //39-59
        ScriptLine(0, 7, "console", "--- COUNTDOWN INITIATED ---", 40),
        ScriptLine(0, 7, "console", "--- 10 SECONDS TO LAUNCH ---", 41),
        ScriptLine(0, 7, "console", "--- 5 SECONDS TO LAUNCH ---", 42),
        ScriptLine(0, 7, "console", "--- 4 SECONDS TO LAUNCH ---", 43),
        ScriptLine(0, 7, "console", "--- 3 SECONDS TO LAUNCH ---", 44),
        ScriptLine(0, 7, "console", "--- 2 SECONDS TO LAUNCH ---", 45),
        ScriptLine(0, 7, "console", "--- 1 SECOND TO LAUNCH ---", 46),
        ScriptLine(0, 7, "console", "--- LAUNCH INITIATED ---", 47),
        ScriptLine(0, 7, "console", "--- BOOSTER STATUS: OK ---", 48),
        ScriptLine(0, 7, "console", "--- BOOSTER SEPARATION: OK ---", 49),
        ScriptLine(0, 7, "console", "--- FIRST STAGE ENGINE LIGHT: OK ---", 50),
        ScriptLine(0, 7, "console", "--- FIRST STAGE FUEL FLOW: OK ---", 51),
        ScriptLine(0, 7, "console", "--- OXYGEN FLOW: OK ---", 52),
        ScriptLine(0, 7, "console", "--- FIRST STAGE SEPARATION: OK ---", 53),
        ScriptLine(0, 7, "console", "--- SECOND STAGE ENGINE LIGHT: OK ---", 54),
        ScriptLine(0, 7, "console", "--- SECOND STAGE FUEL FLOW: OK ---", 55),
        ScriptLine(0, 7, "console", "--- ALL BURNS COMPLETE ---", 56),
        ScriptLine(0, 7, "console", "--- ORBIT TRAJECTORY: OK ---", 57),
        ScriptLine(0, 7, "console", "--- SIGNAL LOST ---", 58),
        ScriptLine(0, 7, "console", "--- ALERT POWER SURGE DETECTED ---", 59),
        ScriptLine(0, 7, "console", "--- 2599 ACTIVE FAILURES ---", 19, "prompt"),

        //60-61
        ScriptLine(0, 7, "console", "--- INTERFACE RUPTURED ---", 61),
        ScriptLine(0, 7, "console", "--- INTERNAL POWER CRITICALLY LOW ---", 21, "prompt"),

        //62
        ScriptLine(0, 7, "console", "--- SHUTDOWN IN PROGRESS ---", 22, "prompt"),

        //63
        ScriptLine(0, 7, "console", "--- CLOSING POWER GATE ---", 0, "end")

        // ScriptLine(0, 7, "script", "", 0),
    )
}

fun getPromptLineList7(): List<PromptLine> {
    return listOf(
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
        PromptLine(0, 7, 22, "At least she made it out safely...", 63)

        // PromptLine(0, 7, 0, "", 0),
    )
}

fun getTriggerList7(): List<Trigger> {
    return listOf(
        Trigger(0, 7, 17, "script", "sound", "mattia_cupelli")

        // Trigger(0, 7, 0, "script", "animation", 0),
    )
}