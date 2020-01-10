package com.javernaut.whatthecodec.presentation.root.viewmodel

import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.*
import androidx.palette.graphics.Palette
import com.hadilq.liveevent.LiveEvent
import com.javernaut.whatthecodec.domain.AudioStream
import com.javernaut.whatthecodec.domain.MediaFile
import com.javernaut.whatthecodec.domain.SubtitleStream
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.AvailableTab
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.BasicVideoInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.sqrt

class MediaFileViewModel(private val frameFullWidth: Int,
                         private val mediaFileProvider: MediaFileProvider,
                         private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private var pendingMediaFileArgument: MediaFileArgument? = null

    private var mediaFile: MediaFile? = null

    private val _basicVideoInfoLiveData = MutableLiveData<BasicVideoInfo?>()
    private val _isFullFeaturedLiveData = MutableLiveData<Boolean>()
    private val _modalProgressLiveData = MutableLiveData<Boolean>()
    private val _framesLiveData = MutableLiveData<Array<Bitmap>>()
    private val _framesBackgroundLiveData = MutableLiveData<Int>(Color.TRANSPARENT)
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
     * True if the file: protocol was used for a media file. False if pipe: protocol was used.
     */
    val isFullFeaturedLiveData: LiveData<Boolean>
        get() = _isFullFeaturedLiveData

    /**
     * Notifies about error during opening a file.
     */
    val errorMessageLiveEvent: LiveData<Boolean>
        get() = _errorMessageLiveEvent

    /**
     * Locks the UI while frames are being read from a media file.
     */
    val modalProgressLiveData: LiveData<Boolean>
        get() = _modalProgressLiveData

    /**
     * Exposes actual Bitmap objects that need to be shown as frames.
     */
    val framesLiveData: LiveData<Array<Bitmap>>
        get() = _framesLiveData

    /**
     * The color for being a background for frames that are shown.
     */
    val framesBackgroundLiveData: LiveData<Int>
        get() = _framesBackgroundLiveData

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
        _isFullFeaturedLiveData.value = mediaFile.videoStream?.fullFeatured ?: true
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
        if (_basicVideoInfoLiveData.value != null) {
            viewModelScope.launch(Dispatchers.Main) {
                _modalProgressLiveData.value = true

                val result = withContext(Dispatchers.IO) {
                    loadFrames()
                }

                _framesBackgroundLiveData.value = result.backgroundColor

                val previousFrames = _framesLiveData.value
                _framesLiveData.value = result.frames
                previousFrames?.forEach { it.recycle() }

                _modalProgressLiveData.value = false
            }
        }
    }

    private fun loadFrames(): VideoProcessingResult {
        val basicInfo = basicVideoInfoLiveData.value!!

        val childFrameWidth = frameFullWidth / sqrt(FRAMES_NUMBER.toDouble()).toInt()
        val childFrameHeight = (childFrameWidth * basicInfo.frameHeight / basicInfo.frameWidth.toDouble()).toInt()

        val bitmaps = Array<Bitmap>(FRAMES_NUMBER) {
            Bitmap.createBitmap(childFrameWidth, childFrameHeight, Bitmap.Config.ARGB_8888)
        }

        mediaFile?.videoStream?.fillWithPreview(bitmaps)

        var backgroundColor: Int = Color.TRANSPARENT
        val palette = Palette.from(bitmaps.first()).generate()
        // Pick the first swatch in this order that isn't null and use its color
        backgroundColor = listOf(
                palette::getDarkMutedSwatch,
                palette::getMutedSwatch,
                palette::getDominantSwatch
        ).firstOrNull {
            it() != null
        }?.invoke()?.rgb ?: backgroundColor

        return VideoProcessingResult(bitmaps, backgroundColor)
    }

    private fun clearPendingUri() {
        pendingMediaFileArgument = null
    }

    private fun MediaFile.toBasicInfo(): BasicVideoInfo? {
        if (videoStream == null) {
            return null
        }
        return BasicVideoInfo(
                fileFormatName,
                videoStream.codecName,
                videoStream.frameWidth,
                videoStream.frameHeight
        )
    }

    companion object {
        const val KEY_MEDIA_FILE_ARGUMENT = "key_video_file_uri"
        const val FRAMES_NUMBER = 4
    }

    private class VideoProcessingResult(
            val frames: Array<Bitmap>,
            val backgroundColor: Int
    )
}