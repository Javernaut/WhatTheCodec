package com.javernaut.whatthecodec.presentation.video.ui.view

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.compose.theme.WhatTheCodecTheme
import com.javernaut.whatthecodec.presentation.compose.theme.secondaryText
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.*
import com.javernaut.whatthecodec.presentation.stream.adapter.GridLayout
import kotlinx.android.synthetic.main.view_preview.view.*
import kotlin.math.min

class PreviewView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    init {
        inflate(context, R.layout.view_preview, this)
    }

    fun setPreview(preview: Preview) {
        framesComposeView.setContent {
            WhatTheCodecTheme {
                when (preview) {
                    is ActualPreview -> {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                // TODO add the animation for the colour change
                                .background(color = Color(preview.backgroundColor)),
                            contentAlignment = Alignment.Center
                        ) {
                            FramesGrid(
                                with(LocalDensity.current) {
                                    Modifier.width(getPreviewViewWidth(context as Activity).toDp())
                                },
                                preview.frames, preview.frameMetrics
                            )
                        }
                    }
                    NoPreviewAvailable -> {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            text = stringResource(id = R.string.page_video_preview_missing_decoder),
                            style = MaterialTheme.typography.subtitle1,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colors.secondaryText
                        )
                    }
                }
            }
        }

    }

    companion object {
        fun getDesiredFrameWidth(activity: Activity): Int {
            val previewWidth = getPreviewViewWidth(activity)

            // 2 (the resource value is only a half of the actual spacing) * 3 (there are 3 such spacings) = 6
            val totalSpacing =
                activity.resources.getDimensionPixelSize(R.dimen.preview_frames_spacing) * 6

            return (previewWidth - totalSpacing) / 2
        }

        fun getPreviewViewWidth(activity: Activity): Int {
            val point = Point()
            activity.windowManager.defaultDisplay.getSize(point)
            return min(point.x, point.y)
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
