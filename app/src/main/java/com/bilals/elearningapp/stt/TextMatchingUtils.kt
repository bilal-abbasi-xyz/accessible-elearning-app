package com.bilals.elearningapp.stt

object TextMatchingUtils {

    private val synonyms = mapOf(
        "programming" to listOf("coding", "software development", "scripting"),
        "math" to listOf("mathematics", "calculus", "algebra"),
        "art" to listOf("painting", "drawing", "design"),
        "science" to listOf("biology", "physics", "chemistry"),
        "cats" to listOf("animals"),
        "history" to listOf("past events", "world history", "ancient history")
    )

    fun replaceSynonyms(input: String): String {
        val words = input.split(" ").toMutableList()
        for (i in words.indices) {
            for ((key, values) in synonyms) {
                if (words[i] in values) words[i] = key  // Replace synonym with main word
            }
        }
        return words.joinToString(" ")
    }

    fun levenshteinDistance(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }

        for (i in s1.indices) dp[i][0] = i
        for (j in s2.indices) dp[0][j] = j

        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = minOf(dp[i - 1][j] + 1, dp[i][j - 1] + 1, dp[i - 1][j - 1] + cost)
            }
        }
        return dp[s1.length][s2.length]
    }
}
