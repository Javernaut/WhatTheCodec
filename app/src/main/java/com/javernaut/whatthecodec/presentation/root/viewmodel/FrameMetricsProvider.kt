package com.javernaut.whatthecodec.presentation.root.viewmodel

import android.content.Context
import androidx.window.layout.WindowMetricsCalculator
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.FrameMetrics
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.math.min
import kotlin.math.roundToInt

class FrameMetricsProvider @Inject constructor(
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

    // there are 3 such spacings (left, middle and right)
    val totalSpacing =
        context.resources.getDimension(R.dimen.preview_frames_spacing) * 3

    return ((previewWidth - totalSpacing) / 2).roundToInt()
}

private fun getPreviewViewWidth(context: Context): Int {
    val metrics = WindowMetricsCalculator.getOrCreate().computeMaximumWindowMetrics(context)
    return with(metrics.bounds) {
        min(width(), height())
    }
}
