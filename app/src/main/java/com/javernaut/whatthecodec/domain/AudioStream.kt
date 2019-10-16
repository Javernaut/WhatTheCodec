package com.javernaut.whatthecodec.domain

class AudioStream(
        val index: Int,
        val codecName: String,
        val title: String?,
        val language: String?,
        val bitRate: Long,
        val sampleFormat: String?,
        val sampleRate: Int,
        val channels: Int,
        val channelLayout: String?,
        val disposition: Int
)