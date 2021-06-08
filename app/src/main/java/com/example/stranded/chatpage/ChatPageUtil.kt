package com.example.stranded.chatpage

//the repository will build a sequence object from the raw database
//tables for that sequence

data class Sequence constructor(
    val scriptLines: List<Line>,
    val sets: List<Set>
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