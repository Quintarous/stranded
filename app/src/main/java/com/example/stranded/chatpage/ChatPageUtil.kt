package com.example.stranded.chatpage

import com.example.stranded.R
import com.example.stranded.database.PromptLine
import com.example.stranded.database.ScriptLine
import com.example.stranded.database.Trigger

//the repository will build a sequence object from the raw database
//tables for that sequence

data class Sequence constructor(
    val scriptLines: List<ScriptLine>,
    val sets: MutableList<Set>,
    val triggers: List<Trigger>
)

data class Set constructor(
    val number: Int,
    val lines: MutableList<PromptLine>
)

data class Line constructor(
    val id: Int,
    val line: String,
    val next: Int,
    val nextType: String
)

//returns a placeholder Line object for testing purposes
//fun placeholderLine(id: Int): Line {
//    return Line(
//        id,
//        "bruh",
//        2,
//        "script"
//    )
//}

fun placeholderPromptLine(id: Int, set: Int): PromptLine {
    return PromptLine(
        id,
        1,
        set,
        "bruh prompt",
        2,
        "script"
    )
}

// returns a placeholder Set object for testing purposes
fun placeholderSet(id: Int): Set {
    val linesList = mutableListOf(
        placeholderPromptLine(1, id),
        placeholderPromptLine(2, id)
    )

    return Set(id, linesList)
}

fun getResourceId(name: String): Int {

    return when (name) {
        "g_up" -> R.drawable.g_meter_up_animation
        "g_walk" -> R.drawable.g_walk_animation
        "g_down" -> R.drawable.g_meter_down_animation
        "g_down_small" -> R.drawable.g_down_small_animation
        "g_run" -> R.drawable.g_run_animation
        "g_wobble" -> R.drawable.g_wobble_animation

        "fire_birds" -> R.raw.fire_birds
        "waves" -> R.raw.waves
        "river" -> R.raw.river
        "rain_window" -> R.raw.rain_window
        "rain" -> R.raw.rain
        "abandoned_warehouse" -> R.raw.abandoned_warehouse_trim
        else -> R.raw.mattia_cupelli_youll_believe
    }
}