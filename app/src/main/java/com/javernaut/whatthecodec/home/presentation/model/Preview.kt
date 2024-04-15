package com.javernaut.whatthecodec.home.presentation.model

sealed class Preview

data object NotYetEvaluated : Preview()
data object NoPreviewAvailable : Preview()

data class ActualPreview(
    val frameMetrics: FrameMetrics,
    val frames: List<Frame>,
    val backgroundColor: Int
) : Preview()

class FrameMetrics(val width: Int, val height: Int)
