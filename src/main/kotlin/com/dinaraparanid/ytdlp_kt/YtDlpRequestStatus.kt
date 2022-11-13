package com.dinaraparanid.ytdlp_kt

sealed interface YtDlpRequestStatus {
    data class Success<T>(@JvmField val data: T) : YtDlpRequestStatus {
        @Suppress("UNCHECKED_CAST")
        fun <T> castAndGetData() = data as T
    }

    enum class Error : YtDlpRequestStatus {
        NO_INTERNET,
        INCORRECT_URL_LINK,
        UNKNOWN_ERROR,
        STREAM_CONVERSION,
        GEO_RESTRICTED
    }
}