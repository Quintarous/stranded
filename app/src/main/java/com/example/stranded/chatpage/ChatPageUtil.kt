package com.example.stranded.chatpage

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

//returns a placeholder Set object for testing purposes
fun placeholderSet(id: Int): Set {
    val linesList = mutableListOf(
        placeholderPromptLine(1, id),
        placeholderPromptLine(2, id)
    )

    return Set(id, linesList)
}