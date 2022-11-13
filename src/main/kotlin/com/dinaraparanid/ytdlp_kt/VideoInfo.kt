package com.dinaraparanid.ytdlp_kt

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data class VideoInfo(
    /** Usual title of YouTube video */
    @JvmField val title: String,
    /** Duration is seconds */
    @JvmField val duration: Long,
    /** Text under the video if there is any */
    @JvmField val description: String?,
    /** File name of uploaded video */
    @SerialName("_filename") @JvmField val fileName: String,
    /** Url of video cover (image) */
    @SerialName("thumbnail") @JvmField val thumbnailURL: String
)

/**
 * Get [VideoInfo] with [VideoInfo.fileName] without extension.
 * Be careful: if [VideoInfo.fileName] is already without extension,
 * it may remove some part of the fileName
 * (e.g. my.video.mp4 -> my.video, but my.video -> my)
 */
inline val VideoInfo.withFileNameWithoutExt
    get() = VideoInfo(
        title,
        duration,
        description,
        File(fileName).nameWithoutExtension,
        thumbnailURL
    )