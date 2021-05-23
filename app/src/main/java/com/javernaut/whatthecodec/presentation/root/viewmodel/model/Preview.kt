package com.javernaut.whatthecodec.presentation.root.viewmodel.model

sealed class Preview

object NoPreviewAvailable : Preview()

data class ActualPreview(
    val frameMetrics: FrameMetrics,
    val frames: List<Frame>,
    val backgroundColor: Int
) : Preview()

class FrameMetrics(val width: Int, val height: Int)
