package com.javernaut.whatthecodec.presentation.root.viewmodel.model

import com.javernaut.whatthecodec.domain.VideoStream

data class BasicVideoInfo(
        val fileFormat: String,
        val videoStream: VideoStream
)