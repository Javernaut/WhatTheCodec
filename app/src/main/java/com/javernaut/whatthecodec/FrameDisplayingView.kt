package com.javernaut.whatthecodec

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.AttributeSet
import android.widget.ImageView

class FrameDisplayingView(context: Context, attrs: AttributeSet) : ImageView(context, attrs) {

    // TODO move this functionality to a background thread
    fun setVideoConfig(config: VideoFileConfig) {
        val bitmaps = Array<Bitmap>(4) {
            Bitmap.createBitmap(
                    resources.getDimensionPixelSize(R.dimen.preview_width),
                    resources.getDimensionPixelSize(R.dimen.preview_height),
                    Bitmap.Config.ARGB_8888)
        }
        config.fillWithPreview(bitmaps)
        setImageBitmap(bitmaps.last())
    }

    fun showLauncherIcon() {
        val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
        doTheThingWithBitmap(bitmap)
        setImageBitmap(bitmap)
    }

    private external fun doTheThingWithBitmap(bitmap: Bitmap)
}
