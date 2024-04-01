package com.javernaut.whatthecodec.presentation.video

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.ActualFrame
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.DecodingErrorFrame
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.Frame
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.FrameMetrics
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.LoadingFrame
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.PlaceholderFrame

@Composable
fun Frame(
    frame: Frame,
    frameMetrics: FrameMetrics,
    modifier: Modifier = Modifier
) {
    Box(modifier = with(LocalDensity.current) {
        modifier.size(frameMetrics.width.toDp(), frameMetrics.height.toDp())
    }, contentAlignment = Alignment.Center) {
        when (frame) {
            LoadingFrame -> {
                LoadingFrame()
            }

            is ActualFrame -> {
                ActualFrame(frame)
            }

            DecodingErrorFrame -> {
                DecodingErrorFrame()
            }

            PlaceholderFrame -> {
                // Nothing to draw here
            }
        }
    }
}

@Preview
@Composable
private fun DecodingErrorFrame() {
    Text(
        text = stringResource(id = R.string.page_video_preview_frame_decoding_error),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxSize()
            .clip(frameItemClipShape())
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(16.dp)
            .wrapContentHeight(Alignment.CenterVertically)
    )
}

@Composable
private fun ActualFrame(frame: ActualFrame) {
    Image(
        modifier = Modifier
            .fillMaxSize()
            .clip(frameItemClipShape()),
        bitmap = frame.frameData.asImageBitmap(),
        contentDescription = null
    )
}

@Composable
private fun LoadingFrame() {
    CircularProgressIndicator()
}

@Composable
private fun frameItemClipShape() = MaterialTheme.shapes.small
