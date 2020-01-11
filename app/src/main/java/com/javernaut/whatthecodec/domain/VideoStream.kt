package com.javernaut.whatthecodec.domain

import android.graphics.Bitmap
import androidx.annotation.Keep

/**
 * Represents metadata of a video stream in a video file. Allows video frames reading.
 */
class VideoStream(
        val basicInfo: BasicStreamInfo,
        val bitRate: Long,
        val frameWidth: Int,
        val frameHeight: Int,
        val fullFeatured: Boolean,
        // TODO move to another class
        @Keep
        private val nativePointer: Long
) {
    external fun fillWithPreview(bitmap: Array<Bitmap>): Boolean

    external fun release()
}