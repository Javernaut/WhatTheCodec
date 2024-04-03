package com.javernaut.whatthecodec.home.presentation.model

import io.github.javernaut.mediafile.VideoStream

data class BasicVideoInfo(
    val preview: Preview,
    val fileFormat: String,
    val fullFeatured: Boolean,
    val videoStream: VideoStream
)
