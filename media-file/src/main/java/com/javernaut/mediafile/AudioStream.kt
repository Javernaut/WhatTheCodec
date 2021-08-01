package com.javernaut.mediafile

class AudioStream(
    val basicInfo: BasicStreamInfo,
    val bitRate: Long,
    val sampleFormat: String?,
    val sampleRate: Int,
    val channels: Int,
    val channelLayout: String?
)
