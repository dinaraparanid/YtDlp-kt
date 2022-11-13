package com.dinaraparanid.ytdlp_kt

import java.util.HashMap

class YtDlpRequest(
    private var url: String? = null,
    /** Directory in which request will be executed */
    @JvmField var directory: String? = null
) {
    internal val options: MutableMap<String, String?> = HashMap()

    /**
     *  Adds yt-dlp option with optional [value].
     *  You can find all existing options [here](https://github.com/yt-dlp/yt-dlp#usage-and-options)
     *  @param key command to execute
     *  @param value optional argument
     */
    fun setOption(key: String, value: String? = null) = options.set(key, value)

    internal fun buildOptions() =
        StringBuilder()
            .also { builder -> url?.let { builder.append("$it ") } }
            .append(
                value = options
                    .entries
                    .map { (name, valueOrNull) -> name to (valueOrNull ?: "") }
                    .map { (name, value) -> "$name $value".trim() }
                    .map { optionFormatted -> "$optionFormatted " }
                    .toTypedArray()
            )
            .toString()
            .trim()
}