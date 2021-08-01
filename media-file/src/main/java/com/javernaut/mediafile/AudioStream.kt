package com.javernaut.mediafile

class AudioStream(
    val basicInfo: BasicStreamInfo,
    val bitRate: BitRate,
    val sampleFormat: String?,
    val sampleRate: SampleRate,
    val channels: Int,
    val channelLayout: String?
)

typealias SampleRate = Int