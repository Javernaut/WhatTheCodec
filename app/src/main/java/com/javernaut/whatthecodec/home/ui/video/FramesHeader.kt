package com.javernaut.whatthecodec.home.ui.video

import android.util.Size
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.home.presentation.model.ActualPreview
import com.javernaut.whatthecodec.home.presentation.model.Frame
import com.javernaut.whatthecodec.home.presentation.model.NoPreviewAvailable
import com.javernaut.whatthecodec.home.presentation.model.NotYetEvaluated
import com.javernaut.whatthecodec.home.presentation.model.Preview


@Composable
fun FramesHeader(
    preview: Preview,
    modifier: Modifier = Modifier
) {
    when (preview) {
        is NotYetEvaluated -> {}
        is ActualPreview -> {
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center
            ) {
                FramesGrid(
                    preview.frames, preview.frameMetrics
                )
            }
        }

        NoPreviewAvailable -> {
            Text(
                modifier = modifier
                    .padding(top = 16.dp),
                text = stringResource(id = R.string.page_video_preview_missing_decoder),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

}

@Composable
private fun FramesGrid(
    newFrames: List<Frame>,
    frameMetrics: Size
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    if (windowSizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND)) {
        NarrowFramesGrid(newFrames, frameMetrics)
    } else {
        FramesRow(newFrames, frameMetrics)
    }
}

@Composable
private fun FramesRow(
    newFrames: List<Frame>,
    frameMetrics: Size,
) {
    Row(
        horizontalArrangement = spacedBy(16.dp)
    ) {
        newFrames.forEach {
            Frame(
                it, frameMetrics, Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun NarrowFramesGrid(
    newFrames: List<Frame>,
    frameMetrics: Size,
) {
    Column(
        verticalArrangement = spacedBy(16.dp)
    ) {
        newFrames.chunked(2).forEach {
            FramesRow(it, frameMetrics)
        }
    }
}
