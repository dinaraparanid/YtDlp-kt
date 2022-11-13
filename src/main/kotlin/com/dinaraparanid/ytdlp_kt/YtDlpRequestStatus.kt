package com.dinaraparanid.ytdlp_kt

import kotlin.jvm.Throws

sealed interface YtDlpRequestStatus {
    data class Success<T>(@JvmField val data: T) : YtDlpRequestStatus {

        /**
         * This method is provided to overcome java's generic shadowing.
         * It simply converts [data] to [T] type
         * @throws ClassCastException if [data] doesn't implement [T]
         */

        @Suppress("UNCHECKED_CAST")
        @Throws(ClassCastException::class)
        fun <T> castAndGetData() = data as T
    }

    sealed class Error(msg: String) : Exception(msg), YtDlpRequestStatus {
        class NoInternet(msg: String) : Error(msg)
        class IncorrectUrl(msg: String) : Error(msg)
        class StreamConversion(msg: String) : Error(msg)
        class GeoRestricted(msg: String) : Error(msg)
        class UnknownError(msg: String) : Error(msg)
    }
}