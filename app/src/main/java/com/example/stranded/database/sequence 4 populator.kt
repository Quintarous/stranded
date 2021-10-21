package com.example.stranded.database

import com.example.stranded.R

fun getScriptLineList4(): List<ScriptLine>  {
    return listOf(
        //1-13
        ScriptLine(0, 4, "console", "--- BOOT SEQUENCE INITIATING ---", 2),
        ScriptLine(0, 4, "console", "--- BOOT SEQUENCE FAILURE ---", 3),
        ScriptLine(0, 4, "console", "--- PARTS RETURNED NULL ---", 4),
        ScriptLine(0, 4, "console", "ANLR1437_long_term_memory_module", 5),
        ScriptLine(0, 4, "console", "TAISHI943211_optical_sensor_module", 6),
        ScriptLine(0, 4, "console", "VERITECH88_high_capacity_battery_module", 7),
        ScriptLine(0, 4, "console", "--- REBOOTING IN SAFE MODE ---", 8),
        ScriptLine(0, 4, "console", "--- BOOT SUCCESSFUL ---", 9),
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
        ScriptLine(0, 4, "console", "--- NEW INTERFACE DETECTED ---", 95),
        ScriptLine(0, 4, "console", "--- ANTLR MOOSE LOCK CONNECTED ---", 25, "prompt"),

        //96-99
        ScriptLine(0, 4, "console", "--- INTERFACE RUPTURED ---", 97),
        ScriptLine(0, 4, "console", "--- INTERNAL BATTERY EMPTY ---", 98),
        ScriptLine(0, 4, "console", "--- EMERGENCY SHUTDOWN ---", 99),
        ScriptLine(0, 4, "console", "--- SHUTTING POWER GATE ---", 0, "end"),

        //100-101
        ScriptLine(0, 4, "console", "--- NON COMPLY ERROR ---", 101),
        ScriptLine(0, 4, "script", "How's it going Mazu?", 26, "prompt"),

        //102-103
        ScriptLine(0, 4, "console", "--- NON COMPLY ERROR ---", 103),
        ScriptLine(0, 4, "script", "Mazu?!", 27, "prompt"),

        //104-105
        ScriptLine(0, 4, "console", "--- NON COMPLY ERROR ---", 105),
        ScriptLine(0, 4, "script", "Mazu?!", 28, "prompt"),

        //106-107
        ScriptLine(0, 4, "console", "--- NON COMPLY ERROR ---", 107),
        ScriptLine(0, 4, "script", "How's it going Mazu?", 27, "prompt")

        // ScriptLine(0, 4, "script", "", 0),
    )
}

fun getPromptLineList4(): List<PromptLine> {
    return listOf(
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
        PromptLine(0, 4, 25, "send(ccw_rotation_command)", 100),
        PromptLine(0, 4, 25, "send(cw_rotation_command)", 104),
        PromptLine(0, 4, 25, "directConnect(50volts)", 96),

        //set 26
        PromptLine(0, 4, 26, "send(cw_rotation_command)", 102),
        PromptLine(0, 4, 26, "directConnect(50volts)", 96),

        //set 27
        PromptLine(0, 4, 27, "directConnect(50volts)", 96),

        //set 28
        PromptLine(0, 4, 28, "send(ccw_rotation_command)", 106),
        PromptLine(0, 4, 28, "directConnect(50volts)", 96),

        // PromptLine(0, 4, 0, "", 0),
    )
}

fun getTriggerList4(): List<Trigger> {
    return listOf(
        Trigger(0, 4, 9, "script", "animation", R.drawable.g_walk_animation, true),
        Trigger(0, 4, 76, "script", "sound", R.raw.rain1, true),
        Trigger(0, 4, 88, "script", "animation", null),

        // Trigger(0, 4, 0, "", "", 0),
    )
}