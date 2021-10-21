package com.example.stranded.database

import com.example.stranded.R

fun getScriptLineList3(): List<ScriptLine> {
    return listOf(
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
        ScriptLine(0, 3, "script", "And I'm alone again...", 0, "end"),

        //ScriptLine(0, 3, "script", "", 0),
    )
}

fun getPromptLineList3(): List<PromptLine> {
    return listOf(
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

        //PromptLine(0, 3, 0, "", 0),
    )
}

fun getTriggerList3(): List<Trigger> {
    return listOf(
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

        // Trigger(0, 3, 0, "", "", 0),
    )
}