package com.javernaut.whatthecodec

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import kotlin.math.sqrt

class FrameDisplayingView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var originFrameWidth = 0
    private var originFrameHeight = 0

    private var scaledViewHeight = 0

    private var childFramesPerRow = 3
    private var childFramesCount = 9
        set(value) {
            field = value
            childFramesPerRow = sqrt(value.toDouble()).toInt()
        }

    private val frameSpacingBase = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, context.resources.displayMetrics)

    private var childFrameWidth = 0
    private var childFrameHeight = 0

    private var frames: Array<Bitmap>? = null
        set(value) {
            field = value
            requestLayout()
        }

    // TODO move this functionality to a background thread
    fun setVideoConfig(config: VideoFileConfig) {
        originFrameWidth = config.width
        originFrameHeight = config.height

        scaledViewHeight = measuredWidth * originFrameHeight / originFrameWidth

        childFrameWidth = scaleChildFrameDimension(measuredWidth)
        childFrameHeight = scaleChildFrameDimension(scaledViewHeight)

        val bitmaps = Array<Bitmap>(childFramesCount) {
            Bitmap.createBitmap(childFrameWidth, childFrameHeight, Bitmap.Config.ARGB_8888)
        }
        config.fillWithPreview(bitmaps)
        frames = bitmaps
    }

    private fun scaleChildFrameDimension(dimension: Int) = ((dimension - (childFramesPerRow - 1) * frameSpacingBase) / childFramesPerRow).toInt()

    fun showLauncherIcon() {
        val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
        doTheThingWithBitmap(bitmap)
        frames = arrayOf(bitmap)
    }

    private external fun doTheThingWithBitmap(bitmap: Bitmap)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val targetWidth = MeasureSpec.getSize(widthMeasureSpec)
        setMeasuredDimension(targetWidth, scaledViewHeight)
    }

    override fun onDraw(canvas: Canvas) {
        frames?.let {
            val bitmapsPerRow = sqrt(it.size.toDouble()).toInt()
            it.forEachIndexed { index, bitmap ->
                val childFrameXPos = index.rem(bitmapsPerRow)
                val left = childFrameXPos * childFrameWidth + childFrameXPos * frameSpacingBase

                val childFrameYPos = index / bitmapsPerRow
                val top = childFrameYPos * childFrameHeight + childFrameYPos * frameSpacingBase

                canvas.drawBitmap(bitmap, left, top, null)
            }
        }
    }
}
