package com.javernaut.whatthecodec.presentation.root.viewmodel

import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.*
import androidx.palette.graphics.Palette
import com.hadilq.liveevent.LiveEvent
import com.javernaut.whatthecodec.domain.AudioStream
import com.javernaut.whatthecodec.domain.FrameLoader
import com.javernaut.whatthecodec.domain.MediaFile
import com.javernaut.whatthecodec.domain.SubtitleStream
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MediaFileViewModel(private val desiredFrameWidth: Int,
                         private val mediaFileProvider: MediaFileProvider,
                         private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private var pendingMediaFileArgument: MediaFileArgument? = null

    private var mediaFile: MediaFile? = null

    private lateinit var frameMetrics: FrameMetrics

    private val _basicVideoInfoLiveData = MutableLiveData<BasicVideoInfo?>()
    private val _isFullFeaturedLiveData = MutableLiveData<Boolean>()
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

    // TODO Consider moving to another class
    private var cachedFrameBitmaps = emptyList<Bitmap>()

    private fun tryLoadVideoFrames() {
        if (_basicVideoInfoLiveData.value != null) {
            viewModelScope.launch(Dispatchers.Main) {
                _previewLiveData.value = Preview(true,
                        frameMetrics,
                        listOf(LoadingFrame, LoadingFrame, LoadingFrame, LoadingFrame),
                        Color.TRANSPARENT
                )

                val result = withContext(Dispatchers.IO) {
                    loadFrames()
                }

                _previewLiveData.value = Preview(true,
                        frameMetrics,
                        result.frames.map { ActualFrame(it) },
                        result.backgroundColor)

                cachedFrameBitmaps.forEach { it.recycle() }
                cachedFrameBitmaps = result.frames.toList()
            }
        }
    }

    private fun loadFrames(): VideoProcessingResult {
        val bitmaps = Array<Bitmap>(FrameLoader.TOTAL_FRAMES_TO_LOAD) {
            Bitmap.createBitmap(frameMetrics.width, frameMetrics.height, Bitmap.Config.ARGB_8888)
        }

        // TODO publish the progress of each frame
        for (i in 0..3) {
            val bitmap = bitmaps[i]
            mediaFile?.frameLoader?.loadNextFrameInto(bitmap)
        }
        mediaFile?.release()

        return VideoProcessingResult(bitmaps, computeBackground(bitmaps.first()))
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
                videoStream
        )
    }

    companion object {
        const val KEY_MEDIA_FILE_ARGUMENT = "key_video_file_uri"
    }

    private class VideoProcessingResult(
            val frames: Array<Bitmap>,
            val backgroundColor: Int
    )
}