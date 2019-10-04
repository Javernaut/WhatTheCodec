package com.javernaut.whatthecodec.domain

import android.graphics.Bitmap
import android.os.ParcelFileDescriptor

class VideoFileConfig private constructor(val fullFeatured: Boolean) {

    private constructor(fileDescriptor: Int) : this(false) {
        nativeNewFD(fileDescriptor)
    }

    private constructor(filePath: String) : this(true) {
        nativeNewPath(filePath)
    }

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

    external fun release()

    external fun fillWithPreview(bitmap: Array<Bitmap>): Boolean

    private external fun nativeNewFD(fileDescriptor: Int)

    private external fun nativeNewPath(filePath: String)

    companion object {

        fun create(filePath: String) = returnIfValid(VideoFileConfig(filePath))

        fun create(descriptor: ParcelFileDescriptor) = returnIfValid(VideoFileConfig(descriptor.detachFd()))

        private fun returnIfValid(config: VideoFileConfig) =
                if (config.nativePointer == -1L) {
                    null
                } else config

        init {
            // The order of importing is mandatory, because otherwise the app will crash on Android API 16 and 17.
            // See: https://android.googlesource.com/platform/bionic/+/master/android-changes-for-ndk-developers.md#changes-to-library-dependency-resolution
            System.loadLibrary("avutil")
            System.loadLibrary("avcodec")
            System.loadLibrary("avformat")
            System.loadLibrary("swscale")
            System.loadLibrary("video-config")
        }
    }
}
