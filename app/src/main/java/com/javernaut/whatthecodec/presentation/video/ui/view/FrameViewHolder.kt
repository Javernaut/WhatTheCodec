package com.javernaut.whatthecodec.presentation.video.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.recyclerview.widget.RecyclerView
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.compose.theme.WhatTheCodecTheme
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.*

class FrameViewHolder(private val containerView: ComposeView) :
    RecyclerView.ViewHolder(containerView) {

    fun bind(frame: Frame) {
        containerView.setContent {
            WhatTheCodecTheme {

            }
        }
    }
}

@Composable
fun Frame(modifier: Modifier, frame: Frame, frameMetrics: FrameMetrics) {
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

@Composable
private fun DecodingErrorFrame() {
    Text(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.colorPreviewDecodingError))
            .padding(16.dp),
        text = stringResource(id = R.string.page_video_preview_frame_decoding_error),
        style = MaterialTheme.typography.caption,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun ActualFrame(frame: ActualFrame) {
    Image(
        modifier = Modifier
            .fillMaxSize(),
        bitmap = frame.frameData.asImageBitmap(),
        contentDescription = null
    )
}

@Composable
private fun LoadingFrame() {
    Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            modifier = Modifier.size(40.dp),
            color = MaterialTheme.colors.secondary
        )
    }
}
