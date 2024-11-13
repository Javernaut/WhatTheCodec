package com.javernaut.whatthecodec.home.presentation

import android.graphics.Bitmap
import android.graphics.Color
import androidx.palette.graphics.Palette
import com.javernaut.whatthecodec.di.DefaultDispatcher
import com.javernaut.whatthecodec.di.IoDispatcher
import com.javernaut.whatthecodec.home.presentation.model.ActualFrame
import com.javernaut.whatthecodec.home.presentation.model.ActualPreview
import com.javernaut.whatthecodec.home.presentation.model.DecodingErrorFrame
import com.javernaut.whatthecodec.home.presentation.model.Frame
import com.javernaut.whatthecodec.home.presentation.model.FrameMetrics
import com.javernaut.whatthecodec.home.presentation.model.LoadingFrame
import com.javernaut.whatthecodec.home.presentation.model.NoPreviewAvailable
import com.javernaut.whatthecodec.home.presentation.model.NotYetEvaluated
import com.javernaut.whatthecodec.home.presentation.model.PlaceholderFrame
import com.javernaut.whatthecodec.home.presentation.model.Preview
import io.github.javernaut.mediafile.factory.MediaFileContext
import io.github.javernaut.mediafile.getFrameLoader
import io.github.javernaut.mediafile.model.MediaFile
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PreviewLoaderHelper @Inject constructor(
    private val frameMetricsProvider: FrameMetricsProvider,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
) {
    fun flowFor(
        mediaFileContext: MediaFileContext,
        mediaFile: MediaFile
    ): Flow<Preview> = flow {
        emit(NotYetEvaluated)

        mediaFileContext.getFrameLoader(framesToLoad).use {
            if (it == null) {
                emit(NoPreviewAvailable)
            } else {
                val frames = MutableList<Frame>(framesToLoad) { PlaceholderFrame }

                val videoStream = mediaFile.videoStream!!
                val metrics = frameMetricsProvider.getTargetFrameMetrics(
                    videoStream.frameWidth,
                    videoStream.frameHeight
                )

                emit(
                    // Initial state where all cells are empty
                    ActualPreview(
                        metrics,
                        frames.toList(),
                        Color.TRANSPARENT
                    )
                )

                for (index in 0 until framesToLoad) {
                    videoFrameFlow(metrics, it::loadNextFrameInto).collect {
                        frames[index] = it
                        emit(
                            ActualPreview(
                                metrics,
                                frames.toList(),
                                Color.TRANSPARENT
                            )
                        )
                    }
                }

                // In the end, we compute the background for frames
                val frameBackground = withContext(defaultDispatcher) {
                    (frames.first() as? ActualFrame)?.frameData?.let(::computeBackground)
                } ?: Color.TRANSPARENT

                emit(
                    ActualPreview(
                        metrics,
                        frames.toList(),
                        frameBackground
                    )
                )
            }
        }
    }

    private fun videoFrameFlow(
        frameMetrics: FrameMetrics,
        frameLoader: (Bitmap) -> Boolean
    ): Flow<Frame> = flow {
        emit(LoadingFrame)
        val frameBitmap =
            Bitmap.createBitmap(frameMetrics.width, frameMetrics.height, Bitmap.Config.ARGB_8888)

        emit(
            if (frameLoader(frameBitmap)) {
                ActualFrame(frameBitmap)
            } else {
                DecodingErrorFrame
            }
        )
    }.flowOn(ioDispatcher)

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

    companion object {
        private const val framesToLoad = 4
    }
}
