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
data class ScriptLine constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val sequence: Int,
    val type: String,
    val line: String,
    val next: Int,
    val nextType: String = "script"
)

@Entity
data class PromptLine constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val sequence: Int,
    val set: Int,
    val line: String,
    val next: Int,
    val nextType: String = "script"
)

@Entity
data class Trigger constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val sequence: Int,
    val triggerId: Int,
    val triggerType: String,
    val resourceType: String,
    val resourceId: String?,
    val loop: Boolean = false,
    val oneAndDone: Boolean = false
)

/*
table for storing the users "save game"
 */
@Entity
data class UserSave constructor(
    @PrimaryKey
    val id: Int,
    var isPowered: Boolean,
    var letterDuration: Int = 70,
    var sequence: Int,
    var line: Int,
    var lineType: String = "script",
)

@Entity
data class PromptResult constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val result: Int
)