package com.javernaut.whatthecodec.presentation.video

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.compose.common.GridLayout
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.ActualPreview
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.Frame
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.FrameMetrics
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.NoPreviewAvailable
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.NotYetEvaluated
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.Preview


@Composable
fun FramesHeader(
    preview: Preview,
    modifier: Modifier = Modifier
) {
    when (preview) {
        is NotYetEvaluated -> {}
        is ActualPreview -> {
            val backgroundColor by animateColorAsState(Color(preview.backgroundColor))
            Box(
                modifier = modifier
                    .background(color = backgroundColor),
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
    frameMetrics: FrameMetrics,
    modifier: Modifier = Modifier,
) {
    val previewFrameSpacing = dimensionResource(id = R.dimen.preview_frames_spacing)
    GridLayout(
        modifier = modifier
            .padding(previewFrameSpacing),
        columns = 2,
        horizontalSpacing = previewFrameSpacing,
        verticalSpacing = previewFrameSpacing
    ) {
        newFrames.forEach {
            Frame(
                it, frameMetrics
            )
        }
    }
}
