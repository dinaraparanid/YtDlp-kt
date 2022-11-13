package com.dinaraparanid.ytdlp_kt

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data class VideoInfo(
    @JvmField val title: String,
    @JvmField val duration: Long,
    @JvmField val description: String,
    @SerialName("_filename") @JvmField val fileName: String,
    @SerialName("thumbnail") @JvmField val thumbnailURL: String
)

inline val VideoInfo.withFileNameWithoutExt
    get() = VideoInfo(
        title,
        duration,
        description,
        File(fileName).nameWithoutExtension,
        thumbnailURL
    )