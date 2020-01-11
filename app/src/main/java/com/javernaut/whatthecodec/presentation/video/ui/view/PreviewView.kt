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
import androidx.recyclerview.widget.GridLayoutManager
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.ActualPreview
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.NoPreviewAvailable
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.Preview
import com.javernaut.whatthecodec.util.setVisible
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_preview.view.*
import kotlin.math.min

class PreviewView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs), LayoutContainer {

    override val containerView = this

    init {
        val commonSpacing = resources.getDimensionPixelSize(R.dimen.preview_frames_spacing)
        setPadding(commonSpacing, commonSpacing, commonSpacing, commonSpacing)

        inflate(context, R.layout.view_preview, this)

        framesRecyclerView.layoutManager = GridLayoutManager(context, 2)
    }

    private val framesAdapter = FramesAdapter().also {
        framesRecyclerView.adapter = it
    }

    fun setPreview(preview: Preview) {
        setVisibilities(preview != NoPreviewAvailable)
        when (preview) {
            NoPreviewAvailable -> {
                applyBackground(Color.TRANSPARENT)
            }
            is ActualPreview -> {
                framesAdapter.setFrames(preview.frames, preview.frameMetrics)
                applyBackground(preview.backgroundColor)
            }
        }
    }

    private fun applyBackground(color: Int) {
        val currentColor = (background as? ColorDrawable)?.color
                ?: Color.TRANSPARENT
        ObjectAnimator.ofObject(this,
                "backgroundColor",
                ArgbEvaluator(),
                currentColor,
                color
        )
                .setDuration(300)
                .start()
    }

    private fun setVisibilities(decodingAvailable: Boolean) {
        framesRecyclerView.setVisible(decodingAvailable)
        decodingUnavailableView.setVisible(!decodingAvailable)
    }

    companion object {
        fun getDesiredFrameWidth(activity: Activity): Int {
            val point = Point()
            activity.windowManager.defaultDisplay.getSize(point)
            val minSide = min(point.x, point.y)

            // 2 (the resource value is only a half of the actual spacing) * 3 (there are 3 such spacings) = 6
            val totalSpacing = activity.resources.getDimensionPixelSize(R.dimen.preview_frames_spacing) * 6

            return (minSide - totalSpacing) / 2
        }
    }
}
