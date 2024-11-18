package com.javernaut.whatthecodec.home.presentation

import android.content.Context
import android.util.Size
import androidx.window.layout.WindowMetricsCalculator
import com.javernaut.whatthecodec.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.math.min
import kotlin.math.roundToInt

class FrameMetricsProvider @Inject constructor(
    @ApplicationContext context: Context
) {
    private val desiredFrameWidth = getDesiredFrameWidth(context)

    fun getTargetFrameMetrics(originalSize: Size): Size {
        val desiredFrameHeight =
            (desiredFrameWidth * originalSize.height / originalSize.width.toDouble()).toInt()

        return Size(desiredFrameWidth, desiredFrameHeight)
    }
}

private fun getDesiredFrameWidth(context: Context): Int {
    val previewWidth = getPreviewViewWidth(context)

    // there are 3 such spacings (left, middle and right)
    val totalNumberOfSpacings = 3
    val totalSpacing =
        context.resources.getDimension(R.dimen.preview_frames_spacing) * totalNumberOfSpacings

    return ((previewWidth - totalSpacing) / 2).roundToInt()
}

private fun getPreviewViewWidth(context: Context): Int {
    val metrics = WindowMetricsCalculator.getOrCreate().computeMaximumWindowMetrics(context)
    return with(metrics.bounds) {
        min(width(), height())
    }
}
