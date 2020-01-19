package com.javernaut.whatthecodec.presentation.root.viewmodel

import androidx.lifecycle.*
import com.hadilq.liveevent.LiveEvent
import com.javernaut.whatthecodec.domain.AudioStream
import com.javernaut.whatthecodec.domain.MediaFile
import com.javernaut.whatthecodec.domain.SubtitleStream
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.*

class MediaFileViewModel(private val desiredFrameWidth: Int,
                         private val mediaFileProvider: MediaFileProvider,
                         private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private var pendingMediaFileArgument: MediaFileArgument? = null

    private var mediaFile: MediaFile? = null
    private var frameLoaderHelper: FrameLoaderHelper? = null

    private lateinit var frameMetrics: FrameMetrics

    private val _basicVideoInfoLiveData = MutableLiveData<BasicVideoInfo?>()
    private val _previewLiveData = MutableLiveData<Preview>()
    private val _errorMessageLiveEvent = LiveEvent<Boolean>()
    private val _availableTabsLiveData = MutableLiveData<List<AvailableTab>>()
    private val _audioStreamsLiveData = MutableLiveData<List<AudioStream>>()
    private val _subtitleStreamsLiveData = MutableLiveData<List<SubtitleStream>>()

    init {
        pendingMediaFileArgument = savedStateHandle[KEY_MEDIA_FILE_ARGUMENT]
    }

    /**
     * Basic info about video stream. See [BasicVideoInfo] for details.
     */
    val basicVideoInfoLiveData: LiveData<BasicVideoInfo?>
        get() = _basicVideoInfoLiveData

    /**
     * Notifies about error during opening a file.
     */
    val errorMessageLiveEvent: LiveData<Boolean>
        get() = _errorMessageLiveEvent

    /**
     * Exposes actual Bitmap objects that need to be shown as frames.
     */
    val previewLiveData: LiveData<Preview>
        get() = _previewLiveData

    /**
     * Tabs that should be visible in UI.
     */
    val availableTabsLiveData: LiveData<List<AvailableTab>>
        get() = _availableTabsLiveData

    /**
     * List of [AudioStream] object for displaying in UI.
     */
    val audioStreamsLiveData: LiveData<List<AudioStream>>
        get() = _audioStreamsLiveData

    /**
     * List of [SubtitleStream] object for displaying in UI.
     */
    val subtitleStreamsLiveData: LiveData<List<SubtitleStream>>
        get() = _subtitleStreamsLiveData

    override fun onCleared() {
        mediaFile?.release()
        frameLoaderHelper?.release()
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
            mediaFile?.release()
            mediaFile = newMediaFile
            applyMediaFile(newMediaFile)
        } else {
            _errorMessageLiveEvent.value = true
        }
    }

    private fun applyMediaFile(mediaFile: MediaFile) {
        _basicVideoInfoLiveData.value = mediaFile.toBasicInfo()
        frameMetrics = computeFrameMetrics()

        frameLoaderHelper?.release()
        frameLoaderHelper = null
        if (mediaFile.supportsFrameLoading()) {
            frameLoaderHelper = FrameLoaderHelper(frameMetrics, viewModelScope)
        }

        _audioStreamsLiveData.value = mediaFile.audioStreams
        _subtitleStreamsLiveData.value = mediaFile.subtitleStreams

        setupTabsAvailable(mediaFile)
        tryLoadVideoFrames()
    }

    private fun setupTabsAvailable(mediaFile: MediaFile) {
        val tabs = mutableListOf<AvailableTab>()
        if (mediaFile.videoStream != null) {
            tabs.add(AvailableTab.VIDEO)
        }
        if (mediaFile.audioStreams.isNotEmpty()) {
            tabs.add(AvailableTab.AUDIO)
        }
        if (mediaFile.subtitleStreams.isNotEmpty()) {
            tabs.add(AvailableTab.SUBTITLES)
        }
        _availableTabsLiveData.value = tabs
    }

    private fun tryLoadVideoFrames() {
        if (frameLoaderHelper != null) {
            frameLoaderHelper?.loadFrames(mediaFile!!) {
                _previewLiveData.postValue(it)
            }
        } else {
            _previewLiveData.value = NoPreviewAvailable
        }
    }

    private fun clearPendingUri() {
        pendingMediaFileArgument = null
    }

    private fun computeFrameMetrics(): FrameMetrics {
        val basicVideoInfo = _basicVideoInfoLiveData.value!!

        val desiredFrameHeight = (desiredFrameWidth * basicVideoInfo.videoStream.frameHeight / basicVideoInfo.videoStream.frameWidth.toDouble()).toInt()

        return FrameMetrics(desiredFrameWidth, desiredFrameHeight)
    }

    private fun MediaFile.toBasicInfo(): BasicVideoInfo? {
        if (videoStream == null) {
            return null
        }
        return BasicVideoInfo(
                fileFormatName,
                fullFeatured,
                videoStream
        )
    }

    companion object {
        const val KEY_MEDIA_FILE_ARGUMENT = "key_video_file_uri"
    }
}