package com.javernaut.whatthecodec.home.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javernaut.whatthecodec.feature.settings.api.content.ContentSettingsRepository
import com.javernaut.whatthecodec.feature.settings.api.content.VideoStreamFeature
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
import io.github.javernaut.mediafile.MediaFileFrameLoader
import io.github.javernaut.mediafile.factory.MediaFileContext
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
    private val contentSettingsRepository: ContentSettingsRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var mediaFileContext: MediaFileContext? = null

    private var _preview = MutableStateFlow<Preview>(NotYetEvaluated)
    private var _mediaFile = MutableStateFlow<MediaFile?>(null)
    private var _screenState = MutableStateFlow<ScreenState?>(null)
    private var frameLoaderHelper: FrameLoaderHelper? = null

    private val _screenMessageChannel = Channel<ScreenMessage>()

    init {
        viewModelScope.launch {
            combine(
                _mediaFile.onEach {
                    // Resetting the preview on each setting of a new MediaFile
                    _preview.value = NotYetEvaluated
                },
                _preview,
                contentSettingsRepository.videoStreamFeatures,
                contentSettingsRepository.audioStreamFeatures,
                contentSettingsRepository.subtitleStreamFeatures,
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

        savedStateHandle.get<MediaFileArgument>(KEY_MEDIA_FILE_ARGUMENT)?.let {
            openMediaFile(it)
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
        // Release prev context, frame loader and frame loader wrapper
        // TODO Dispose frame loading too
        mediaFileContext?.dispose()
    }

    fun openMediaFile(argument: MediaFileArgument) {
        val newMediaFileContext = mediaFileProvider.obtainMediaFile(argument)
        if (newMediaFileContext == null) {
            sendMessage(ScreenMessage.FileOpeningError)
            return
        }

        val newMediaFile = newMediaFileContext.readMetaData()
        if (newMediaFile == null) {
            sendMessage(ScreenMessage.FileOpeningError)
            return
        }

        savedStateHandle[KEY_MEDIA_FILE_ARGUMENT] = argument

        // Release prev context, frame loader and frame loader wrapper
        // TODO Dispose frame loading too
        mediaFileContext?.dispose()

        mediaFileContext = newMediaFileContext
        _mediaFile.value = newMediaFile

        val newMediaFileFrameLoader =
            MediaFileFrameLoader.create(newMediaFileContext, FRAMES_TO_READ)
        if (newMediaFileFrameLoader != null) {
            // setup new frame loader wrapper
            val frameMetrics = computeFrameMetrics()

            frameLoaderHelper = FrameLoaderHelper(frameMetrics!!, viewModelScope, ::applyPreview)
            frameLoaderHelper?.loadFrames(newMediaFileFrameLoader)
        } else {
            applyPreview(NoPreviewAvailable)
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

    private fun applyPreview(preview: Preview) {
        _preview.value = preview
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
        private const val FRAMES_TO_READ = 4
    }
}
