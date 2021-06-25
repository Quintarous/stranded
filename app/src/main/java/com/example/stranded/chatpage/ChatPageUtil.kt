package com.example.stranded.chatpage

//the repository will build a sequence object from the raw database
//tables for that sequence

data class Sequence constructor(
    val scriptLines: List<Line>,
    val sets: List<Set>,
    val triggers: List<Trigger>
)

data class Set constructor(
    val number: Int,
    val lines: List<Line>
)

data class Line constructor(
    val id: Int,
    val line: String,
    val next: Int,
    val nextType: String
)

data class Trigger constructor(
    val triggerId: Int,
    val triggerType: String,
    val action: String,
    val resourceType: String?,
    val resourceId: Int?,
    val loop: Boolean?
)

//returns a placeholder Line object for testing purposes
fun placeholderLine(id: Int): Line {
    return Line(
        id,
        "bruh",
        2,
        "script"
    )
}

//returns a placeholder Set object for testing purposes
fun placeholderSet(): Set {
    val linesList = listOf<Line>(
        placeholderLine(1),
        placeholderLine(2),
        placeholderLine(3)
    )

    return Set(
        1,
        linesList
    )
}