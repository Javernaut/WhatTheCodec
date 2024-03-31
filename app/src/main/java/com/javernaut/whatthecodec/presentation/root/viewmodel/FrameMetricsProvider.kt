package com.javernaut.whatthecodec.presentation.root.viewmodel

import android.content.Context
import androidx.window.layout.WindowMetricsCalculator
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.FrameMetrics
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.math.min

class FrameMetricsProvider @Inject constructor(
    // TODO App context isn't suitable for getting the window metrics
    @ApplicationContext context: Context
) {
    private val desiredFrameWidth = getDesiredFrameWidth(context)

    fun getTargetFrameMetrics(originalWidth: Int, originalHeight: Int): FrameMetrics {
        val desiredFrameHeight =
            (desiredFrameWidth * originalHeight / originalWidth.toDouble()).toInt()

        return FrameMetrics(desiredFrameWidth, desiredFrameHeight)
    }
}

private fun getDesiredFrameWidth(context: Context): Int {
    val previewWidth = getPreviewViewWidth(context)

    // 2 (the resource value is only a half of the actual spacing) * 3 (there are 3 such spacings) = 6
    val totalSpacing =
        context.resources.getDimensionPixelSize(R.dimen.preview_frames_spacing) * 6

    return (previewWidth - totalSpacing) / 2
}

// TODO Make private
fun getPreviewViewWidth(context: Context): Int {
    val metrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(context)
    return with(metrics.bounds) {
        min(width(), height())
    }
}