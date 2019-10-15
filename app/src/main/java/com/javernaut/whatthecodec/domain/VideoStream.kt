package com.javernaut.whatthecodec.domain

import android.graphics.Bitmap
import androidx.annotation.Keep

/**
 * Represents metadata of a video stream in a video file. Allows video frames reading.
 */
class VideoStream(
        val frameWidth: Int,
        val frameHeight: Int,
        val codecName: String,
        @Keep
        val fullFeatured: Boolean,
        @Keep
        private val nativePointer: Long
) {
    external fun fillWithPreview(bitmap: Array<Bitmap>): Boolean

    external fun release()
}