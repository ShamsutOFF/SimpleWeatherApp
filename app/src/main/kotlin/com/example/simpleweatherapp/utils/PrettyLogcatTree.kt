package com.example.simpleweatherapp.utils

import android.annotation.SuppressLint
import android.util.Log
import timber.log.Timber

@SuppressLint("LogNotTimber")
class PrettyLogcatTree : Timber.DebugTree() {
    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?,
    ) {
        val actualTag = tag ?: "APP"

        if (message.length < 2000) {
            Log.println(priority, actualTag, message)
        } else {
            for (chunk in chunkedCustom(message, 1700, 2000).withIndex()) {
                Log.println(priority, actualTag, "[${chunk.index}]: ${chunk.value}")
            }
        }
    }

    private fun chunkedCustom(
        message: String,
        chunkMinSize: Int,
        chunkMaxSize: Int,
        delimiters: List<Char> = listOf(' ', ',', ';', '-', '\n'),
    ): List<String> {
        var remaining = message
        val result = mutableListOf<String>()

        while (remaining.length > chunkMaxSize) {
            val chunkEnd = findBestSplitPosition(remaining, chunkMinSize, chunkMaxSize, delimiters)
            result.add(remaining.take(chunkEnd))
            remaining = remaining.substring(chunkEnd)
        }
        result.add(remaining)
        return result
    }

    private fun findBestSplitPosition(
        text: String,
        min: Int,
        max: Int,
        delimiters: List<Char>,
    ): Int {
        for (i in max downTo min) {
            if (i < text.length && text[i] in delimiters) {
                return i + 1
            }
        }
        return max
    }
}
