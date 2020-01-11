package com.javernaut.whatthecodec.presentation.video.ui.view

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.Preview
import kotlin.math.min

// TODO Hide the RecyclerView and the background as implementation details
class FrameDisplayingView(context: Context, attrs: AttributeSet) : RecyclerView(context, attrs) {

    init {
        val commonSpacing = resources.getDimensionPixelSize(R.dimen.preview_frames_spacing)
        setPadding(commonSpacing, commonSpacing, commonSpacing, commonSpacing)
    }

    private val framesAdapter = FramesAdapter().also {
        adapter = it
    }

    fun setPreview(preview: Preview) {
        // TODO use all the data from preview object

        // TODO consider height of this view when frames are not decoded at all. It should be fixed

        layoutManager = GridLayoutManager(context, 2)

        framesAdapter.setFrames(preview.frames, preview.frameMetrics)
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
