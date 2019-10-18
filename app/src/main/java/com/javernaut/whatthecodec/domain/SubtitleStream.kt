package com.javernaut.whatthecodec.domain

class SubtitleStream(
        val index: Int,
        val codecName: String,
        val disposition: Int,
        val title: String?,
        val language: String?
)