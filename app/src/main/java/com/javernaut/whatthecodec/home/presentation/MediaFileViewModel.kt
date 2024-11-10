package com.javernaut.whatthecodec.home.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javernaut.whatthecodec.feature.settings.api.content.ContentSettingsRepository
import com.javernaut.whatthecodec.feature.settings.api.content.VideoStreamFeature
import com.javernaut.whatthecodec.home.domain.FileReadingUseCase
import com.javernaut.whatthecodec.home.presentation.model.AudioPage
import com.javernaut.whatthecodec.home.presentation.model.NotYetEvaluated
import com.javernaut.whatthecodec.home.presentation.model.Preview
import com.javernaut.whatthecodec.home.presentation.model.ScreenMessage
import com.javernaut.whatthecodec.home.presentation.model.ScreenState
import com.javernaut.whatthecodec.home.presentation.model.SubtitlesPage
import com.javernaut.whatthecodec.home.presentation.model.VideoPage
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.javernaut.mediafile.MediaFile
import io.github.javernaut.mediafile.factory.MediaFileContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MediaFileViewModel @Inject constructor(
    private val clipboard: Clipboard,
    private val previewLoaderHelper: PreviewLoaderHelper,
    private val fileReadingUseCase: FileReadingUseCase,
    private val contentSettingsRepository: ContentSettingsRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var mediaFileContext: MediaFileContext? = null

    private var _preview = MutableStateFlow<Preview>(NotYetEvaluated)
    private var _mediaFile = MutableStateFlow<MediaFile?>(null)
    private var _screenState = MutableStateFlow<ScreenState?>(null)

    private val _screenMessageChannel = Channel<ScreenMessage>()

    init {
        viewModelScope.launch {
            combine(
                _mediaFile,
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
//        mediaFileContext?.dispose()
    }

    private var lastJob: Job? = null

    fun openMediaFile(argument: MediaFileArgument) {
        lastJob?.cancel()
        lastJob = viewModelScope.launch {
            val (newMediaFileContext, newMediaFile) = fileReadingUseCase
                .readFile(argument)
                .getOrElse {
                    sendMessage(ScreenMessage.FileOpeningError)
                    return@launch
                }

            savedStateHandle[KEY_MEDIA_FILE_ARGUMENT] = argument

            // Release prev context, frame loader and frame loader wrapper
            // TODO Dispose frame loading too
//            mediaFileContext?.dispose()

            mediaFileContext = newMediaFileContext
            _mediaFile.value = newMediaFile

            previewLoaderHelper.flowFor(newMediaFileContext, newMediaFile).collect(_preview)
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
