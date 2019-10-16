package com.javernaut.whatthecodec.domain

class AudioStream(
        val index: Int,
        val codecName: String,
        val title: String?,
        val language: String?,
        val disposition: Int
)