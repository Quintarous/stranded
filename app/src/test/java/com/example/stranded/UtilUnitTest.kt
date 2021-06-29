package com.example.stranded

import com.example.stranded.chatpage.Set
import com.example.stranded.chatpage.placeholderPromptLine
import org.junit.Test

class UtilUnitTest {

    private val PromptLines = listOf(
        placeholderPromptLine(1, 1),
        placeholderPromptLine(2, 1),
        placeholderPromptLine(3, 2)
    )

    @Test
    fun createSetsList_ReturnsCorrectly() {
        val result = createSetsList(PromptLines)

        val expected = mutableListOf(
            Set(1, mutableListOf(placeholderPromptLine(1, 1), placeholderPromptLine(2, 1))),
            Set(2, mutableListOf(placeholderPromptLine(3, 2)))
        )

        assert(result == expected)
    }
}