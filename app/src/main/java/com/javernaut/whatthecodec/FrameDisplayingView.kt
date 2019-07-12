package com.javernaut.whatthecodec

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import kotlin.math.sqrt

class FrameDisplayingView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var frames: Array<Bitmap>? = null
        set(value) {
            field = value
            // TODO consider view size change in order to support different aspect rations of frames
            invalidate()
        }

    // TODO move this functionality to a background thread
    fun setVideoConfig(config: VideoFileConfig) {
        val bitmaps = Array<Bitmap>(4) {
            Bitmap.createBitmap(
                    // TODO consider sizes relatively to the view size itself and spaces between frames
                    resources.getDimensionPixelSize(R.dimen.preview_width) / 2,
                    resources.getDimensionPixelSize(R.dimen.preview_height) / 2,
                    Bitmap.Config.ARGB_8888)
        }
        config.fillWithPreview(bitmaps)
        frames = bitmaps
    }

    fun showLauncherIcon() {
        val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
        doTheThingWithBitmap(bitmap)
        frames = arrayOf(bitmap)
    }

    private external fun doTheThingWithBitmap(bitmap: Bitmap)

    override fun onDraw(canvas: Canvas) {
        frames?.let {
            val bitmapsPerRow = sqrt(it.size.toDouble()).toInt()
            it.forEachIndexed { index, bitmap ->
                val left = index.rem(bitmapsPerRow) * bitmap.width
                val top = index / bitmapsPerRow * bitmap.height
                canvas.drawBitmap(bitmap, left.toFloat(), top.toFloat(), null)
            }
        }
    }
}
