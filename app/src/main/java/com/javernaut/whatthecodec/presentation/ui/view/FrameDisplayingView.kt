package com.javernaut.whatthecodec.presentation.ui.view

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.widget.ImageView
import androidx.gridlayout.widget.GridLayout
import kotlin.math.sqrt

class FrameDisplayingView(context: Context, attrs: AttributeSet) : GridLayout(context, attrs) {

    private var childFramesPerRow = 1

    var childFramesCount = 1
        set(value) {
            field = value
            childFramesPerRow = sqrt(value.toDouble()).toInt()
        }

    fun setFrames(frames: Array<Bitmap>) {
        removeAllViews()

        rowCount = childFramesPerRow
        columnCount = childFramesPerRow

        for (row in 0 until childFramesPerRow) {
            for (column in 0 until childFramesPerRow) {
                addView(ImageView(context))
            }
        }

        for (index in 0 until childCount) {
            (getChildAt(index) as ImageView).setImageBitmap(frames[index])
        }
    }
}
