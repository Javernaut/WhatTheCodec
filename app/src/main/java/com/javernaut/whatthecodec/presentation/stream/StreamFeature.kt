package com.javernaut.whatthecodec.presentation.stream

import android.content.res.Resources
import io.github.javernaut.mediafile.MediaStream

interface StreamFeature<T : MediaStream> {
    val key: Int
    val title: Int
    fun getValue(stream: T, resources: Resources): String?
}