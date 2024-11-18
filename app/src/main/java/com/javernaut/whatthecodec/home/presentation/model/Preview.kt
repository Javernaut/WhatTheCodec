package com.javernaut.whatthecodec.home.presentation.model

import android.util.Size

sealed class Preview

data object NotYetEvaluated : Preview()
data object NoPreviewAvailable : Preview()

data class ActualPreview(
    val frameMetrics: Size,
    val frames: List<Frame>,
    val backgroundColor: Int
) : Preview()
