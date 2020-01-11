package com.javernaut.whatthecodec.domain

import android.graphics.Bitmap


class FrameLoader(private var frameLoadingContextHandle: Long) {

    private var framesDecoded = 0

    fun loadNextFrameInto(bitmap: Bitmap): Boolean {
        val result = nativeLoadFrame(frameLoadingContextHandle, framesDecoded, bitmap)
        framesDecoded++
        return result
    }

    fun release() {
        nativeRelease(frameLoadingContextHandle)
        frameLoadingContextHandle = -1
    }

    companion object {
        const val TOTAL_FRAMES_TO_LOAD = 4

        @JvmStatic
        private external fun nativeRelease(handle: Long)

        @JvmStatic
        private external fun nativeLoadFrame(handle: Long, index: Int, bitmap: Bitmap): Boolean
    }
}