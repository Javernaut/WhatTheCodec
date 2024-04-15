package com.javernaut.whatthecodec.home.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javernaut.whatthecodec.home.data.StreamFeatureRepository
import com.javernaut.whatthecodec.home.data.model.VideoStreamFeature
import com.javernaut.whatthecodec.home.presentation.model.AudioPage
import com.javernaut.whatthecodec.home.presentation.model.FrameMetrics
import com.javernaut.whatthecodec.home.presentation.model.NoPreviewAvailable
import com.javernaut.whatthecodec.home.presentation.model.NotYetEvaluated
import com.javernaut.whatthecodec.home.presentation.model.Preview
import com.javernaut.whatthecodec.home.presentation.model.ScreenMessage
import com.javernaut.whatthecodec.home.presentation.model.ScreenState
import com.javernaut.whatthecodec.home.presentation.model.SubtitlesPage
import com.javernaut.whatthecodec.home.presentation.model.VideoPage
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.javernaut.mediafile.MediaFile
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MediaFileViewModel @Inject constructor(
    private val clipboard: Clipboard,
    private val frameMetricsProvider: FrameMetricsProvider,
    private val mediaFileProvider: MediaFileProvider,
    private val streamFeatureRepository: StreamFeatureRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var pendingMediaFileArgument: MediaFileArgument? = null

    private var _preview = MutableStateFlow<Preview>(NotYetEvaluated)
    private var _mediaFile = MutableStateFlow<MediaFile?>(null)
    private var _screenState = MutableStateFlow<ScreenState?>(null)
    private var frameLoaderHelper: FrameLoaderHelper? = null

    private val _screenMessageChannel = Channel<ScreenMessage>()

    init {
        pendingMediaFileArgument = savedStateHandle[KEY_MEDIA_FILE_ARGUMENT]

        viewModelScope.launch {
            combine(
                _mediaFile.onEach {
                    // Resetting the preview on each setting of a new MediaFile
                    _preview.value = NotYetEvaluated
                },
                _preview,
                streamFeatureRepository.videoStreamFeatures,
                streamFeatureRepository.audioStreamFeatures,
                streamFeatureRepository.subtitleStreamFeatures,
            ) { mediaFile, preview, videoFeatures, audioFeatures, subtitleFeatures ->
                mediaFile?.let {
                    ScreenState(
                        videoPage = mediaFile.toVideoPage(preview, videoFeatures),

                        audioPage = mediaFile.audioStreams.takeIf { it.isNotEmpty() }
                            ?.let { AudioPage(it, audioFeatures) },

                        subtitlesPage = mediaFile.subtitleStreams.takeIf { it.isNotEmpty() }
                            ?.let { SubtitlesPage(it, subtitleFeatures) }
                    )
                }
            }.collect(_screenState)
        }
    }

    /**
     * Exposes the whole info about the selected media file
     */
    val screenState: StateFlow<ScreenState?> = _screenState


    /**
     * One time notifications about important events.
     */
    val screenMessage = _screenMessageChannel.receiveAsFlow()

    override fun onCleared() {
        if (frameLoaderHelper == null) {
            _mediaFile.value?.release()
        } else {
            frameLoaderHelper?.release()
        }
    }

    fun applyPendingMediaFileIfNeeded() {
        if (pendingMediaFileArgument != null) {
            openMediaFile(pendingMediaFileArgument!!)
        }
    }

    fun openMediaFile(argument: MediaFileArgument) {
        clearPendingUri()

        val newMediaFile = mediaFileProvider.obtainMediaFile(argument)
        if (newMediaFile != null) {
            savedStateHandle.set(KEY_MEDIA_FILE_ARGUMENT, argument)

            releasePreviousMediaFileAndFrameLoader(newMediaFile)

            _mediaFile.value = newMediaFile
            applyMediaFile(newMediaFile)
        } else {
            sendMessage(ScreenMessage.FileOpeningError)
        }
    }

    fun copyToClipboard(value: String) {
        clipboard.copy(value)
        sendMessage(ScreenMessage.ValueCopied(value))
    }

    fun onPermissionDenied() {
        sendMessage(ScreenMessage.PermissionDeniedError)
    }

    private fun sendMessage(message: ScreenMessage) {
        viewModelScope.launch {
            _screenMessageChannel.send(message)
        }
    }

    private fun applyMediaFile(mediaFile: MediaFile) {
        val frameMetrics = computeFrameMetrics()

        frameLoaderHelper = if (mediaFile.supportsFrameLoading())
            FrameLoaderHelper(frameMetrics!!, viewModelScope, ::applyPreview)
        else null

        tryLoadVideoFrames(mediaFile)
    }

    private fun releasePreviousMediaFileAndFrameLoader(newMediaFile: MediaFile) {
        if (frameLoaderHelper != null) {
            frameLoaderHelper?.release(newMediaFile.supportsFrameLoading())
        } else {
            // MediaFile is released only if there was no FrameLoaderHelper used.
            _mediaFile.value?.release()
        }
        frameLoaderHelper = null
    }

    private fun tryLoadVideoFrames(mediaFile: MediaFile) {
        if (frameLoaderHelper != null) {
            frameLoaderHelper?.loadFrames(mediaFile)
        } else {
            applyPreview(NoPreviewAvailable)
        }
    }

    private fun applyPreview(preview: Preview) {
        _preview.value = preview
    }

    private fun clearPendingUri() {
        pendingMediaFileArgument = null
    }

    private fun computeFrameMetrics(): FrameMetrics? {
        val videoStream = _mediaFile.value?.videoStream

        return videoStream?.let {
            frameMetricsProvider.getTargetFrameMetrics(
                it.frameWidth,
                it.frameHeight
            )
        }
    }

    private fun MediaFile.toVideoPage(
        preview: Preview,
        streamFeatures: Set<VideoStreamFeature>
    ): VideoPage? {
        return videoStream?.let {
            VideoPage(
                preview,
                fileFormatName,
                fullFeatured,
                it,
                streamFeatures
            )
        }
    }

    companion object {
        const val KEY_MEDIA_FILE_ARGUMENT = "key_video_file_uri"
    }
}
