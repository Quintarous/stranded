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
data class SequenceScripts constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val sequence: Int,
    val consoleLine: Boolean = false,
    val line: String,
    val next: Int,
    val nextType: String = "script"
)

@Entity
data class SequencePrompts constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val sequence: Int,
    val set: Int,
    val line: String,
    val next: Int,
    val nextType: String = "script"
)

/*
table for storing the users "save game" note it will also have columns for every prompt set in the
most prompt heavy sequence so it can track user choices from the currently saved sequence
 */
@Entity
data class UserSave constructor(
    @PrimaryKey
    val id: Int,
    val isPowered: Boolean,
    val sequence: Int,
    val line: Int,
    val lineType: String = "script"
)