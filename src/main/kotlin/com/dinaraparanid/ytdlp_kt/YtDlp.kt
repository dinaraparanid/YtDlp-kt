package com.dinaraparanid.ytdlp_kt

import kotlinx.coroutines.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.IOException

object YtDlp {
    private val updaterScope = CoroutineScope(Dispatchers.IO)
    private val fetcherScope = CoroutineScope(Dispatchers.IO)
    private val json = Json { ignoreUnknownKeys = true }

    @Volatile
    private var isYoutubeDLUpdateTaskStarted = false

    private fun buildCommand(command: String) = "yt-dlp $command"

    @JvmStatic
    @JvmName("executeWithResponse")
    @Throws(YtDlpException::class)
    private fun executeWithResponse(request: YtDlpRequest): YtDlpResponse {
        val directory = request.directory
        val options = request.options
        val outBuffer = StringBuffer() //stdout
        val errBuffer = StringBuffer() //stderr

        val startTime = System.nanoTime()
        val command = buildCommand(request.buildOptions())
        val commandArr = java.lang.String(buildCommand(request.buildOptions())).split(" ")

        val processBuilder = ProcessBuilder(*commandArr).also { builder ->
            directory
                ?.let(::File)
                ?.let(builder::directory)
        }

        val process = try {
            processBuilder.start()
        } catch (e: IOException) {
            throw YtDlpException(e)
        }

        val outStream = process.inputStream
        val errStream = process.errorStream

        StreamGobbler(outBuffer, outStream)
        StreamGobbler(errBuffer, errStream)

        val exitCode = try {
            process.waitFor()
        } catch (e: InterruptedException) {
            throw YtDlpException(e)
        }

        val out = outBuffer.toString()
        val err = errBuffer.toString()

        if (exitCode > 0)
            throw YtDlpException(err)

        val elapsedTime = ((System.nanoTime() - startTime) / 1000000).toInt()
        return YtDlpResponse(command, options, directory, exitCode, elapsedTime, out, err)
    }

    @JvmStatic
    @JvmName("execute")
    fun execute(request: YtDlpRequest) =
        kotlin.runCatching {
            YtDlpRequestStatus.Success(executeWithResponse(request))
        }.getOrElse {  exception ->
            ConversionException(exception).error
        }

    @JvmStatic
    @JvmName("updateAsync")
    fun updateAsync() = updaterScope.launch(Dispatchers.IO) {
        if (isYoutubeDLUpdateTaskStarted)
            return@launch

        isYoutubeDLUpdateTaskStarted = true
        Runtime.getRuntime().exec("yt-dlp -U")
        isYoutubeDLUpdateTaskStarted = false
    }

    @JvmStatic
    @JvmName("getVideoData")
    fun getVideoData(url: String) =
        kotlin.runCatching {
            YtDlpRequest(url)
                .apply {
                    setOption("dump-json")
                    setOption("no-playlist")
                }
                .let(YtDlp::executeWithResponse)
                .let(YtDlpResponse::out)
                .let<String, VideoInfo>(json::decodeFromString)
                .withFileNameWithoutExt
                .let(YtDlpRequestStatus::Success)
        }.getOrElse { exception ->
            ConversionException(exception).error
        }

    @Throws(YtDlpException::class)
    fun getVideoDataAsync(url: String) = fetcherScope.async(Dispatchers.IO) {
        getVideoData(url)
    }
}