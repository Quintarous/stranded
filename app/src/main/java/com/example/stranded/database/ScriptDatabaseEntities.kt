package com.example.stranded.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/*
tables for the sequence lines
every sequence will have two tables

1. a script table for the script lines
2. a prompts table for the user prompt lines

prompt lines are grouped into sets which is how they are referenced

every line contains navigation data in the "next" and "nextType" properties
whether the "nextType" of a given line is "set" or "script" determines if the app will show a
script line or a set of prompts next
 */
@Entity
data class SequenceOneScript constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val consoleLine: Boolean = false,
    val line: String,
    val next: Int,
    val nextType: String = "script"
)

@Entity
data class SequenceOnePrompts constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val set: Int,
    val line: String,
    val next: Int,
    val nextType: String = "script"
)