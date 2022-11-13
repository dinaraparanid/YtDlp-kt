package com.dinaraparanid.ytdlp_kt

data class YtDlpResponse(
    /** Command that was executed in the request */
    @JvmField val command: String,
    /** Options that were provided in the request */
    @JvmField val options: Map<String, String?>,
    /** Directory in which the response was executed */
    @JvmField val directory: String?,
    /** 0 if successful, > 0 if not */
    @JvmField val exitCode: Int,
    /** Full time between sending request and achieving response */
    @JvmField val elapsedTime: Int,
    /** Output from stdout */
    @JvmField val out: String,
    /** Output from stderr */
    @JvmField val err: String
)

