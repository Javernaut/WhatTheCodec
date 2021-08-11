package com.javernaut.whatthecodec.presentation.root.viewmodel.model

import io.github.javernaut.mediafile.VideoStream

data class BasicVideoInfo(
    val fileFormat: String,
    val fullFeatured: Boolean,
    val videoStream: VideoStream
)
