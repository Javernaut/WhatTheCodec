package com.javernaut.mediafile

import androidx.annotation.Keep

@Keep
class BasicStreamInfo(
    val index: Int,
    val title: String?,
    val codecName: String,
    val language: String?,
    val disposition: Int
)

typealias BitRate = Long