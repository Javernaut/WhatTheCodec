package com.javernaut.whatthecodec.presentation.video.ui.view

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.compose.theme.WhatTheCodecTheme
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.*
import com.javernaut.whatthecodec.presentation.stream.adapter.GridLayout
import com.javernaut.whatthecodec.util.setVisible
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_preview.view.*
import kotlin.math.min

class PreviewView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs),
    LayoutContainer {

    override val containerView = this

    init {
        inflate(context, R.layout.view_preview, this)
    }

    fun setPreview(preview: Preview) {
        setVisibilities(preview != NoPreviewAvailable)
        when (preview) {
            NoPreviewAvailable -> {
                applyBackground(Color.TRANSPARENT)
            }
            is ActualPreview -> {
                framesComposeView.setContent {
                    WhatTheCodecTheme {
                        FramesGrid(
                            with(LocalDensity.current) {
                                Modifier.width(getPreviewViewWidth(context as Activity).toDp())
                            },
                            preview.frames, preview.frameMetrics
                        )
                    }
                }
                applyBackground(preview.backgroundColor)
            }
        }
    }

    private fun applyBackground(color: Int) {
        val currentColor = (background as? ColorDrawable)?.color
            ?: Color.TRANSPARENT
        ObjectAnimator.ofObject(
            this,
            "backgroundColor",
            ArgbEvaluator(),
            currentColor,
            color
        )
            .setDuration(300)
            .start()
    }

    private fun setVisibilities(decodingAvailable: Boolean) {
        framesComposeView.setVisible(decodingAvailable)
        decodingUnavailableView.setVisible(!decodingAvailable)
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
