package com.javernaut.whatthecodec.home.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javernaut.whatthecodec.home.presentation.model.AvailableTab
import com.javernaut.whatthecodec.home.presentation.model.BasicVideoInfo
import com.javernaut.whatthecodec.home.presentation.model.FrameMetrics
import com.javernaut.whatthecodec.home.presentation.model.NoPreviewAvailable
import com.javernaut.whatthecodec.home.presentation.model.NotYetEvaluated
import com.javernaut.whatthecodec.home.presentation.model.Preview
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.javernaut.mediafile.AudioStream
import io.github.javernaut.mediafile.MediaFile
import io.github.javernaut.mediafile.SubtitleStream
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MediaFileViewModel @Inject constructor(
    private val frameMetricsProvider: FrameMetricsProvider,
    private val mediaFileProvider: MediaFileProvider,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var pendingMediaFileArgument: MediaFileArgument? = null

    private var mediaFile: MediaFile? = null
    private var frameLoaderHelper: FrameLoaderHelper? = null

    private val _screenState = MutableLiveData<ScreenState?>()
    private val _screenMessageChannel = Channel<ScreenMessage>()

    init {
        pendingMediaFileArgument = savedStateHandle[KEY_MEDIA_FILE_ARGUMENT]
    }

    /**
     * Exposes the whole info about the selected media file
     */
    val screenState: LiveData<ScreenState?>
        get() = _screenState

    /**
     * Notifies about error during opening a file.
     */
    val screenMessage = _screenMessageChannel.receiveAsFlow()

    override fun onCleared() {
        if (frameLoaderHelper == null) {
            mediaFile?.release()
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

            mediaFile = newMediaFile
            applyMediaFile(newMediaFile)
        } else {
            viewModelScope.launch {
                _screenMessageChannel.send(ScreenMessage.FileOpeningError)
            }
        }
    }

    private fun applyMediaFile(mediaFile: MediaFile) {
        _screenState.value = ScreenState(
            mediaFile.toBasicInfo(),
            mediaFile.audioStreams,
            mediaFile.subtitleStreams
        )
        val frameMetrics = computeFrameMetrics()

        frameLoaderHelper = if (mediaFile.supportsFrameLoading())
            FrameLoaderHelper(frameMetrics!!, viewModelScope, ::applyPreview)
        else null

        tryLoadVideoFrames()
    }

    private fun releasePreviousMediaFileAndFrameLoader(newMediaFile: MediaFile) {
        if (frameLoaderHelper != null) {
            frameLoaderHelper?.release(newMediaFile.supportsFrameLoading())
        } else {
            // MediaFile is released only if there was no FrameLoaderHelper used.
            mediaFile?.release()
        }
        frameLoaderHelper = null
    }

    private fun tryLoadVideoFrames() {
        if (frameLoaderHelper != null) {
            frameLoaderHelper?.loadFrames(mediaFile!!)
        } else {
            applyPreview(NoPreviewAvailable)
        }
    }

    private fun applyPreview(preview: Preview) {
        _screenState.value = _screenState.value?.let {
            it.copy(
                videoPage = it.videoPage?.copy(
                    preview = preview
                )
            )
        }
    }

    private fun clearPendingUri() {
        pendingMediaFileArgument = null
    }

    private fun computeFrameMetrics(): FrameMetrics? {
        val basicVideoInfo = _screenState.value?.videoPage

        return basicVideoInfo?.let {
            frameMetricsProvider.getTargetFrameMetrics(
                it.videoStream.frameWidth,
                it.videoStream.frameHeight
            )
        }
    }

    private fun MediaFile.toBasicInfo(): BasicVideoInfo? {
        return videoStream?.let {
            BasicVideoInfo(
                NotYetEvaluated,
                fileFormatName,
                fullFeatured,
                it
            )
        }
    }

    companion object {
        const val KEY_MEDIA_FILE_ARGUMENT = "key_video_file_uri"
    }
}

data class ScreenState(
    val videoPage: BasicVideoInfo?,
    val audioPage: List<AudioStream>?,
    val subtitlesPage: List<SubtitleStream>?
) {
    val availableTabs = buildList {
        if (videoPage != null) {
            add(AvailableTab.VIDEO)
        }
        if (!audioPage.isNullOrEmpty()) {
            add(AvailableTab.AUDIO)
        }
        if (!subtitlesPage.isNullOrEmpty()) {
            add(AvailableTab.SUBTITLES)
        }
    }
}

sealed interface ScreenMessage {
    data object FileOpeningError : ScreenMessage
}
