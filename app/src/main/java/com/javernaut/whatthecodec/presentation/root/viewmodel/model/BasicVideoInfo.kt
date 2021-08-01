package com.javernaut.whatthecodec.presentation.root.viewmodel.model

import com.javernaut.mediafile.VideoStream

data class BasicVideoInfo(
    val fileFormat: String,
    val fullFeatured: Boolean,
    val videoStream: VideoStream
)
