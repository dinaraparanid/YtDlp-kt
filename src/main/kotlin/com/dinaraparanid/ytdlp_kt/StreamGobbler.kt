package com.dinaraparanid.ytdlp_kt

import java.io.IOException
import java.io.InputStream

internal class StreamGobbler(private val buffer: StringBuffer, private val stream: InputStream) : Thread() {
    init {
        start()
    }

    override fun run() {
        try {
            var nextChar: Int

            while (stream.read().also { nextChar = it } != -1)
                buffer.append(nextChar.toChar())
        } catch (_: IOException) {
        }
    }
}