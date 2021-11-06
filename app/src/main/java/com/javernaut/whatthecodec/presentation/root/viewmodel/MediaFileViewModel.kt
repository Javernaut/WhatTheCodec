package com.javernaut.whatthecodec.presentation.root.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hadilq.liveevent.LiveEvent
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.AvailableTab
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.BasicVideoInfo
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.FrameMetrics
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.NoPreviewAvailable
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.NotYetEvaluated
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.Preview
import io.github.javernaut.mediafile.AudioStream
import io.github.javernaut.mediafile.MediaFile
import io.github.javernaut.mediafile.SubtitleStream

class MediaFileViewModel(
    private val desiredFrameWidth: Int,
    private val mediaFileProvider: MediaFileProvider,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var pendingMediaFileArgument: MediaFileArgument? = null

    private var mediaFile: MediaFile? = null
    private var frameLoaderHelper: FrameLoaderHelper? = null

    private var frameMetrics: FrameMetrics? = null

    private val _screenState = MutableLiveData<ScreenState?>()
    private val _errorMessageLiveEvent = LiveEvent<Boolean>()

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
    val errorMessageLiveEvent: LiveData<Boolean>
        get() = _errorMessageLiveEvent

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
            _errorMessageLiveEvent.value = true
        }
    }

    private fun applyMediaFile(mediaFile: MediaFile) {
        _screenState.value = ScreenState(
            mediaFile.toBasicInfo(),
            mediaFile.audioStreams,
            mediaFile.subtitleStreams
        )
        frameMetrics = computeFrameMetrics()

        if (mediaFile.supportsFrameLoading()) {
            frameLoaderHelper = FrameLoaderHelper(frameMetrics!!, viewModelScope, ::applyPreview)
        }

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
            val frameHeight = it.videoStream.frameHeight
            val frameWidth = it.videoStream.frameWidth.toDouble()
            val desiredFrameHeight = (desiredFrameWidth * frameHeight / frameWidth).toInt()

            FrameMetrics(desiredFrameWidth, desiredFrameHeight)
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
    val availableTabs: List<AvailableTab>
        get() = mutableListOf<AvailableTab>().apply {
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