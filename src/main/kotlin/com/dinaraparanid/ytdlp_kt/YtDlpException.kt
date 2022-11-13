package com.dinaraparanid.ytdlp_kt

class YtDlpException(message: String?) : Exception(message) {
    constructor(e: Throwable) : this(e.message)
}