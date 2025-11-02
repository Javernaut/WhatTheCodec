package com.javernaut.whatthecodec.home.ui.video

import android.util.Size
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.compose.common.GridLayout
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
    frameMetrics: Size,
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
