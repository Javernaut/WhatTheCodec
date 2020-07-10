package com.javernaut.whatthecodec.domain

class AudioStream(
        val basicInfo: BasicStreamInfo,
        val bitRate: Long,
        val sampleFormat: String?,
        val sampleRate: Int,
        val channels: Int,
        val channelLayout: String?)
