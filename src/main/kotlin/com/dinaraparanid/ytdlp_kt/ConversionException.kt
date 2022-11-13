package com.dinaraparanid.ytdlp_kt

import java.io.PrintWriter
import java.io.StringWriter

internal class ConversionException(cause: Throwable) : Exception(cause) {
    @JvmField
    internal val error = errorType
}

private inline val Throwable.errorType: YtDlpRequestStatus.Error
    get() {
        val stringWriter = StringWriter()
        val printWriter = PrintWriter(stringWriter)

        printStackTrace()
        printStackTrace(printWriter)
        val stackTrack = stringWriter.toString()

        return when {
            "Unable to download" in stackTrack -> YtDlpRequestStatus.Error.NoInternet(stackTrack)

            "is not a valid URL" in stackTrack ->
                YtDlpRequestStatus.Error.IncorrectUrl(stackTrack)

            "video available in your country" in stackTrack ->
                YtDlpRequestStatus.Error.GeoRestricted(stackTrack)

            "Unexpected symbol '.' in numeric literal at path: \$.duration" in stackTrack ->
                YtDlpRequestStatus.Error.StreamConversion(stackTrack)

            else -> YtDlpRequestStatus.Error.UnknownError(stackTrack)
        }
    }