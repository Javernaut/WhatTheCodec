package com.javernaut.whatthecodec.home.presentation

import android.graphics.Bitmap
import android.graphics.Color
import androidx.palette.graphics.Palette
import com.javernaut.whatthecodec.home.presentation.model.ActualFrame
import com.javernaut.whatthecodec.home.presentation.model.ActualPreview
import com.javernaut.whatthecodec.home.presentation.model.DecodingErrorFrame
import com.javernaut.whatthecodec.home.presentation.model.Frame
import com.javernaut.whatthecodec.home.presentation.model.FrameMetrics
import com.javernaut.whatthecodec.home.presentation.model.LoadingFrame
import com.javernaut.whatthecodec.home.presentation.model.PlaceholderFrame
import io.github.javernaut.mediafile.LegacyFrameLoader
import io.github.javernaut.mediafile.MediaFileFrameLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FrameLoaderHelper(
    private val metrics: FrameMetrics,
    private val scope: CoroutineScope,
    private val previewApplier: (ActualPreview) -> Unit
) {

    private var cachedFrameBitmaps = HashMap<Int, Bitmap>()

    private var isWorking = false

    private lateinit var framesLoadingJob: Job

    fun loadFrames(mediaFileFrameLoader: MediaFileFrameLoader) {
        isWorking = true

        framesLoadingJob = scope.launch(Dispatchers.Default) {
            val frames = mutableListOf<Frame>(
                PlaceholderFrame,
                PlaceholderFrame,
                PlaceholderFrame,
                PlaceholderFrame
            )

            // Initial state where all cells are empty
            var actualPreview = ActualPreview(
                metrics,
                frames,
                Color.TRANSPARENT
            )

            tryApplyPreview(previewApplier, actualPreview)

            for (index in 0 until LegacyFrameLoader.TOTAL_FRAMES_TO_LOAD) {
                if (!isWorking) {
                    break
                }

                // First, marking a cell as 'loading'
                frames[index] = LoadingFrame
                tryApplyPreview(previewApplier, actualPreview)

                // Loading a frame in a separate dispatcher
                val result = withContext(Dispatchers.IO) {
                    loadSingleFrame(mediaFileFrameLoader, index)
                }

                // Depending on the actual result, displaying either an actual frame or an error
                frames[index] = if (result.success) {
                    ActualFrame(result.frame)
                } else {
                    DecodingErrorFrame
                }

                tryApplyPreview(previewApplier, actualPreview)
            }

            if (isWorking) {
                // In the end, we compute the background for frames
                val frameBackground = withContext(Dispatchers.Default) {
                    computeBackground(getFrameBitmap(0))
                }
                actualPreview = actualPreview.copy(backgroundColor = frameBackground)
                tryApplyPreview(previewApplier, actualPreview)
            }

            mediaFileFrameLoader.dispose()
        }
    }

    private suspend fun tryApplyPreview(
        previewApplier: (ActualPreview) -> Unit,
        preview: ActualPreview
    ) {
        if (isWorking) {
            withContext(Dispatchers.Main) {
                // Enforcing the difference in 'frames' field to be properly recognized as a change
                previewApplier(
                    preview.copy(
                        frames = ArrayList(preview.frames)
                    )
                )
            }
        }
    }

    fun release(needPreviewReset: Boolean = false) {
        isWorking = false

        if (needPreviewReset) {
            previewApplier(
                ActualPreview(
                    metrics,
                    listOf(PlaceholderFrame, PlaceholderFrame, PlaceholderFrame, PlaceholderFrame),
                    Color.TRANSPARENT
                )
            )
        }

        scope.launch(Dispatchers.Default) {
            framesLoadingJob.join()

            cachedFrameBitmaps.clear()
        }
    }

    private fun loadSingleFrame(
        mediaFileFrameLoader: MediaFileFrameLoader,
        index: Int
    ): FrameLoadingResult {
        val frameBitmap = getFrameBitmap(index)

        val successfulLoading = mediaFileFrameLoader.loadNextFrameInto(frameBitmap)

        return FrameLoadingResult(frameBitmap, successfulLoading)
    }

    private fun getFrameBitmap(index: Int): Bitmap {
        if (!cachedFrameBitmaps.containsKey(index)) {
            cachedFrameBitmaps[index] =
                Bitmap.createBitmap(metrics.width, metrics.height, Bitmap.Config.ARGB_8888)
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
