package com.example.stranded.chatpage

import com.example.stranded.R
import com.example.stranded.database.PromptLine
import com.example.stranded.database.ScriptLine
import com.example.stranded.database.Trigger

/**
 * An object that contains all of the data for any given narrative sequence.
 *
 * The repository will build a sequence object from the database on request.
 */
data class Sequence constructor(
    val scriptLines: List<ScriptLine>,
    val sets: MutableList<Set>,
    val triggers: List<Trigger>
)


/**
 * An object with a "set" of PromptLines and a number identifying which one it is within a sequence.
 *
 * A Set is the term I use to refer to a "set" of PromptLines (or dialogue options). A Set of
 * PromptLines are always displayed together.
 */
data class Set constructor(
    val number: Int,
    val lines: MutableList<PromptLine>
)

/*
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
*/

/**
 * Interprets a string into an animation or sound resource.
 *
 * Trigger objects do not contain direct references to the sound effect/animation they are meant
 * to trigger. Because that integer reference changes and triggers need to stay static. So instead
 * triggers contain a string referencing which sound/animation they want to fire and this method
 * translates that string into a usable reference to that sound/animation resource.
 */
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