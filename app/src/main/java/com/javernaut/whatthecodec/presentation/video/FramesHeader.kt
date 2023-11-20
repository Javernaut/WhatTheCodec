package com.javernaut.whatthecodec.presentation.video

import android.app.Activity
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.window.layout.WindowMetricsCalculator
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.compose.common.GridLayout
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.ActualPreview
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.Frame
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.FrameMetrics
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.NoPreviewAvailable
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.NotYetEvaluated
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.Preview
import kotlin.math.min

fun getDesiredFrameWidth(activity: Activity): Int {
    val previewWidth = getPreviewViewWidth(activity)

    // 2 (the resource value is only a half of the actual spacing) * 3 (there are 3 such spacings) = 6
    val totalSpacing =
        activity.resources.getDimensionPixelSize(R.dimen.preview_frames_spacing) * 6

    return (previewWidth - totalSpacing) / 2
}

fun getPreviewViewWidth(activity: Activity): Int {
    val metrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(activity)
    return with(metrics.bounds) {
        min(width(), height())
    }
}

@Composable
fun FramesHeader(preview: Preview, dstFrameWidth: Int) {
    when (preview) {
        is NotYetEvaluated -> {}
        is ActualPreview -> {
            val backgroundColor by animateColorAsState(Color(preview.backgroundColor))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(color = backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                FramesGrid(
                    with(LocalDensity.current) {
                        Modifier.width(dstFrameWidth.toDp())
                    },
                    preview.frames, preview.frameMetrics
                )
            }
        }

        NoPreviewAvailable -> {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    text = stringResource(id = R.string.page_video_preview_missing_decoder),
                    style = MaterialTheme.typography.subtitle1,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }

}

@Composable
fun FramesGrid(modifier: Modifier = Modifier, newFrames: List<Frame>, frameMetrics: FrameMetrics) {
    GridLayout(
        modifier
            .padding(2.dp), 2
    ) {
        newFrames.forEach {
            Frame(
                Modifier
                    .padding(2.dp), it, frameMetrics
            )
        }
    }
}
