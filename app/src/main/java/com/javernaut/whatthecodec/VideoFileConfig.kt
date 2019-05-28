package com.javernaut.whatthecodec

import android.graphics.Bitmap
import android.os.ParcelFileDescriptor

class VideoFileConfig private constructor(fileDescriptor: Int) {

    // The field is handled by the native code
    private val nativePointer: Long = 0

    val fileFormat: String
        external get

    val codecName: String
        external get

    val width: Int
        external get

    val height: Int
        external get

    init {
        nativeNew(fileDescriptor)
    }

    external fun release()

    external fun fillWithPreview(bitmap: Bitmap)

    private external fun nativeNew(fileDescriptor: Int)

    companion object {

        fun create(descriptor: ParcelFileDescriptor): VideoFileConfig? {
            val result = VideoFileConfig(descriptor.detachFd())
            return if (result.nativePointer == -1L) {
                null
            } else result
        }

        init {
            System.loadLibrary("avformat")
            System.loadLibrary("avcodec")
            System.loadLibrary("avutil")
            System.loadLibrary("swscale")
            System.loadLibrary("video-config")
        }
    }
}
