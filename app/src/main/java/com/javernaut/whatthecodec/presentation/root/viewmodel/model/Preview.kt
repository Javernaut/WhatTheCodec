package com.javernaut.whatthecodec.presentation.root.viewmodel.model

class Preview(
        val decodingAvailable: Boolean,
        val frameMetrics: FrameMetrics,
        val frames: List<Frame>,
        val backgroundColor: Int
)

class FrameMetrics(val width: Int, val height: Int)