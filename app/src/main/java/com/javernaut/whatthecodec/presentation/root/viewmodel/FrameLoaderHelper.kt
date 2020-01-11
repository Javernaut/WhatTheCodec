package com.javernaut.whatthecodec.presentation.root.viewmodel

import android.graphics.Bitmap
import android.graphics.Color
import androidx.palette.graphics.Palette
import com.javernaut.whatthecodec.domain.FrameLoader
import com.javernaut.whatthecodec.domain.MediaFile
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FrameLoaderHelper(
        private val metrics: FrameMetrics,
        private val scope: CoroutineScope
) {

    private var cachedFrameBitmaps = HashMap<Int, Bitmap>()

    fun loadFrames(mediaFile: MediaFile, previewApplier: (ActualPreview) -> Unit) {
        scope.launch(Dispatchers.Default) {
            val frames = mutableListOf<Frame>(PlaceholderFrame, PlaceholderFrame, PlaceholderFrame, PlaceholderFrame)

            // Initial state where all cells are empty
            var actualPreview = ActualPreview(
                    metrics,
                    frames,
                    Color.TRANSPARENT
            )

            previewApplier(actualPreview)

            for (index in 0 until FrameLoader.TOTAL_FRAMES_TO_LOAD) {
                // First, marking a cell as 'loading'
                frames[index] = LoadingFrame
                previewApplier(actualPreview)

                // Loading a frame in a separate dispatcher
                val result = withContext(Dispatchers.IO) {
                    loadSingleFrame(mediaFile, index)
                }

                // Depending on the actual result, displaying either an actual frame or an error
                frames[index] = if (result.success) {
                    ActualFrame(result.frame)
                } else {
                    DecodingErrorFrame
                }

                previewApplier(actualPreview)
            }

            // In the end, we compute the background for frames
            val frameBackground = withContext(Dispatchers.Default) {
                computeBackground(getFrameBitmap(0))
            }
            actualPreview = actualPreview.copy(backgroundColor = frameBackground)
            previewApplier(actualPreview)

            mediaFile.release()
        }
    }

    fun release() {
        cachedFrameBitmaps.values.forEach { it.recycle() }
        cachedFrameBitmaps.clear()
    }

    private fun loadSingleFrame(mediaFile: MediaFile, index: Int): FrameLoadingResult {
        val frameBitmap = getFrameBitmap(index)

        val successfulLoading = mediaFile.frameLoader?.loadNextFrameInto(frameBitmap) == true

        return FrameLoadingResult(frameBitmap, successfulLoading)
    }

    private fun getFrameBitmap(index: Int): Bitmap {
        if (!cachedFrameBitmaps.containsKey(index)) {
            cachedFrameBitmaps[index] = Bitmap.createBitmap(metrics.width, metrics.height, Bitmap.Config.ARGB_8888)
        }
        return cachedFrameBitmaps[index]!!
    }

    private fun computeBackground(bitmap: Bitmap): Int {
        val palette = Palette.from(bitmap).generate()
        // Pick the first swatch in this order that isn't null and use its color
        return listOf(
                palette::getDarkMutedSwatch,
                palette::getMutedSwatch,
                palette::getDominantSwatch
        ).firstOrNull {
            it() != null
        }?.invoke()?.rgb ?: Color.TRANSPARENT
    }

    private class FrameLoadingResult(
            val frame: Bitmap,
            val success: Boolean
    )
}