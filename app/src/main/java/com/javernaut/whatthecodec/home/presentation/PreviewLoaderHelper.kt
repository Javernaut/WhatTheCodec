package com.javernaut.whatthecodec.home.presentation

import android.graphics.Bitmap
import android.util.Size
import androidx.core.graphics.createBitmap
import com.javernaut.whatthecodec.di.IoDispatcher
import com.javernaut.whatthecodec.home.presentation.model.ActualFrame
import com.javernaut.whatthecodec.home.presentation.model.ActualPreview
import com.javernaut.whatthecodec.home.presentation.model.DecodingErrorFrame
import com.javernaut.whatthecodec.home.presentation.model.Frame
import com.javernaut.whatthecodec.home.presentation.model.LoadingFrame
import com.javernaut.whatthecodec.home.presentation.model.NoPreviewAvailable
import com.javernaut.whatthecodec.home.presentation.model.NotYetEvaluated
import com.javernaut.whatthecodec.home.presentation.model.PlaceholderFrame
import com.javernaut.whatthecodec.home.presentation.model.Preview
import io.github.javernaut.mediafile.MediaFile
import io.github.javernaut.mediafile.ext.getFrameLoader
import io.github.javernaut.mediafile.model.MediaInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class PreviewLoaderHelper @Inject constructor(
    private val frameMetricsProvider: FrameMetricsProvider,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    fun flowFor(
        mediaFile: MediaFile,
        mediaInfo: MediaInfo
    ): Flow<Preview> = flow {
        emit(NotYetEvaluated)

        mediaFile.getFrameLoader(framesToLoad).use {
            if (it == null) {
                emit(NoPreviewAvailable)
            } else {
                val frames = MutableList<Frame>(framesToLoad) { PlaceholderFrame }

                val videoStream = mediaInfo.videoStream!!
                val metrics = frameMetricsProvider.getTargetFrameMetrics(videoStream.frameSize)

                emit(
                    // Initial state where all cells are empty
                    ActualPreview(
                        metrics,
                        frames.toList()
                    )
                )

                for (index in 0 until framesToLoad) {
                    videoFrameFlow(metrics, it::loadNextFrameInto).collect {
                        frames[index] = it
                        emit(
                            ActualPreview(
                                metrics,
                                frames.toList()
                            )
                        )
                    }
                }
            }
        }
    }

    private fun videoFrameFlow(
        frameMetrics: Size,
        frameLoader: (Bitmap) -> Boolean
    ): Flow<Frame> = flow {
        emit(LoadingFrame)
        val frameBitmap = createBitmap(frameMetrics.width, frameMetrics.height)

        emit(
            if (frameLoader(frameBitmap)) {
                ActualFrame(frameBitmap)
            } else {
                DecodingErrorFrame
            }
        )
    }.flowOn(ioDispatcher)

    companion object {
        private const val framesToLoad = 4
    }
}
