package com.dinaraparanid.ytdlp_kt

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.IOException

object YtDlp : CoroutineScope by CoroutineScope(Dispatchers.IO) {
    private val json = Json { ignoreUnknownKeys = true }

    @Volatile
    private var isYoutubeDLUpdateTaskStarted = false

    private fun buildCommand(command: String) = "yt-dlp $command"

    private fun executeWithResponseOrThrow(request: YtDlpRequest): YtDlpResponse {
        val directory = request.directory
        val options = request.options
        val outBuffer = StringBuffer() //stdout
        val errBuffer = StringBuffer() //stderr

        val startTime = System.nanoTime()
        val command = buildCommand(request.buildOptions())
        val commandArr = java.lang.String(buildCommand(request.buildOptions())).split(" ")

        val processBuilder = ProcessBuilder(*commandArr).also { builder ->
            directory?.let(::File)?.let(builder::directory)
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

    /**
     * Executes provided request or returns
     * [YtDlpRequestStatus.Error] if something went wrong.
     * Blocks current thread until the end of execution
     * @param request request to execute
     * @return [YtDlpRequestStatus.Success] with [YtDlpResponse]
     * or [YtDlpRequestStatus.Error] if something went wrong
     */

    @JvmStatic
    @JvmName("execute")
    fun execute(request: YtDlpRequest) =
        kotlin.runCatching {
            YtDlpRequestStatus.Success(executeWithResponseOrThrow(request))
        }.getOrElse {  exception ->
            ConversionException(exception).error
        }

    /**
     * Executes provided request asynchronously or returns
     * [YtDlpRequestStatus.Error] if something went wrong
     * @param request request to execute
     * @return [YtDlpRequestStatus.Success] with [YtDlpResponse]
     * or [YtDlpRequestStatus.Error] if something went wrong
     */

    @JvmStatic
    @JvmName("executeAsync")
    fun executeAsync(request: YtDlpRequest) = async { execute(request) }

    /**
     * Updates yt-dlp on the device.
     * Blocks current thread until the end of execution
     */

    @JvmStatic
    @JvmName("update")
    fun update() {
        if (isYoutubeDLUpdateTaskStarted)
            return

        isYoutubeDLUpdateTaskStarted = true
        Runtime.getRuntime().exec("yt-dlp -U").waitFor()
        isYoutubeDLUpdateTaskStarted = false
    }

    /** Updates yt-dlp on the device asynchronously */

    @JvmStatic
    @JvmName("updateAsync")
    fun updateAsync() = launch { update() }

    /**
     * Gets [VideoInfo] by url or returns
     * [YtDlpRequestStatus.Error] if something went wrong.
     * Blocks current thread until the end of execution
     * @param url url of searchable video
     * @return [YtDlpRequestStatus.Success] with [VideoInfo]
     * or [YtDlpRequestStatus.Error] if something went wrong
     */

    @JvmStatic
    @JvmName("getVideoData")
    fun getVideoData(url: String) =
        kotlin.runCatching {
            YtDlpRequest(url)
                .apply {
                    setOption("--dump-json")
                    setOption("--no-playlist")
                }
                .let(YtDlp::executeWithResponseOrThrow)
                .let(YtDlpResponse::out)
                .let<String, VideoInfo>(json::decodeFromString)
                .withFileNameWithoutExt
                .let(YtDlpRequestStatus::Success)
        }.getOrElse { exception ->
            ConversionException(exception).error
        }

    /**
     * Gets [VideoInfo] by url asynchronously or returns
     * [YtDlpRequestStatus.Error] if something went wrong.
     * @param url url of searchable video
     * @return [YtDlpRequestStatus.Success] with [VideoInfo]
     * or [YtDlpRequestStatus.Error] if something went wrong
     */

    @JvmStatic
    @JvmName("getVideoDataAsync")
    fun getVideoDataAsync(url: String) = async {
        getVideoData(url)
    }
}