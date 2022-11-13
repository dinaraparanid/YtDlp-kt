package com.dinaraparanid.ytdlp_kt

data class YtDlpResponse(
    @JvmField val command: String,
    @JvmField val options: Map<String, String?>,
    @JvmField val directory: String?,
    @JvmField val exitCode: Int,
    @JvmField val elapsedTime: Int,
    @JvmField val out: String,
    @JvmField val err: String
)

