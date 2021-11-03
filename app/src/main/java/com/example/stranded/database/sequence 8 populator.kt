package com.example.stranded.database

fun getScriptLineList(): List<ScriptLine> {
    return listOf(
        //1-4
        ScriptLine(0, 8, "console", "--- BOOT SEQUENCE INITIATING ---", 2),
        ScriptLine(0, 8, "console", "--- BOOT SEQUENCE SUCCESS ---", 3),
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

        // ScriptLine(0, 8, "script", "", 0),
    )
}

fun getPromptLineList8(): List<PromptLine> {
    return listOf(
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

        // PromptLine(0, 8, 0, "", 0),
    )
}

fun getTriggerList8(): List<Trigger> {
    return listOf(


        // Trigger(0, 8, 0, "script", "animation", 0),
    )
}