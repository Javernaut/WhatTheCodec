package com.javernaut.whatthecodec.presentation.ui.view

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.util.Log
import android.widget.ImageView
import androidx.gridlayout.widget.GridLayout
import com.javernaut.whatthecodec.presentation.viewmodel.FramesToShow
import kotlin.math.sqrt

class FrameDisplayingView(context: Context, attrs: AttributeSet) : GridLayout(context, attrs) {

    fun setFrames(frames: Array<Bitmap>) {
        removeAllViews()

        val childFramesPerRow = sqrt(frames.size.toDouble()).toInt()

        rowCount = childFramesPerRow
        columnCount = childFramesPerRow

        for (bitmap in frames) {
            val imageView = ImageView(context)
            imageView.setImageBitmap(bitmap)
            addView(imageView)
        }
    }
}
