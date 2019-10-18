package com.javernaut.whatthecodec.presentation.root.viewmodel

import android.graphics.Bitmap
import android.graphics.Color
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.palette.graphics.Palette
import com.hadilq.liveevent.LiveEvent
import com.javernaut.whatthecodec.domain.AudioStream
import com.javernaut.whatthecodec.domain.MediaFile
import com.javernaut.whatthecodec.domain.SubtitleStream
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.AvailableTab
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.BasicVideoInfo
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.FramesToShow
import kotlin.math.sqrt

class MediaFileViewModel(private val frameFullWidth: Int,
                         private val mediaFileProvider: MediaFileProvider,
                         private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private var pendingMediaFileUri: String? = null

    private var mediaFile: MediaFile? = null

    private val _basicVideoInfoLiveData = MutableLiveData<BasicVideoInfo?>()
    private val _isFullFeaturedLiveData = MutableLiveData<Boolean>()
    private val _framesToShowNumber = MutableLiveData<FramesToShow>(framesInitialValue())
    private val _modalProgressLiveData = MutableLiveData<Boolean>()
    private val _framesLiveData = MutableLiveData<Array<Bitmap>>()
    private val _framesBackgroundLiveData = MutableLiveData<Int>(Color.TRANSPARENT)
    private val _errorMessageLiveEvent = LiveEvent<Boolean>()
    private val _availableTabsLiveData = MutableLiveData<List<AvailableTab>>()
    private val _audioStreamsLiveData = MutableLiveData<List<AudioStream>>()
    private val _subtitleStreamsLiveData = MutableLiveData<List<SubtitleStream>>()

    init {
        pendingMediaFileUri = savedStateHandle[KEY_VIDEO_FILE_URI]
    }

    val basicVideoInfoLiveData: LiveData<BasicVideoInfo?>
        get() = _basicVideoInfoLiveData

    val isFullFeaturedLiveData: LiveData<Boolean>
        get() = _isFullFeaturedLiveData

    val framesToShowNumber: LiveData<FramesToShow>
        get() = _framesToShowNumber

    val errorMessageLiveEvent: LiveData<Boolean>
        get() = _errorMessageLiveEvent

    val modalProgressLiveData: LiveData<Boolean>
        get() = _modalProgressLiveData

    val framesLiveData: LiveData<Array<Bitmap>>
        get() = _framesLiveData

    val framesBackgroundLiveData: LiveData<Int>
        get() = _framesBackgroundLiveData

    val availableTabsLiveData: LiveData<List<AvailableTab>>
        get() = _availableTabsLiveData

    val audioStreamsLiveData: LiveData<List<AudioStream>>
        get() = _audioStreamsLiveData

    val subtitleStreamsLiveData: LiveData<List<SubtitleStream>>
        get() = _subtitleStreamsLiveData

    override fun onCleared() {
        mediaFile?.release()
    }

    fun applyPendingMediaFileIfNeeded() {
        if (pendingMediaFileUri != null) {
            openMediaFile(pendingMediaFileUri!!)
        }
    }

    fun openMediaFile(uri: String) {
        clearPendingUri()

        val newMediaFile = mediaFileProvider.obtainMediaFile(uri)
        if (newMediaFile != null) {
            savedStateHandle.set(KEY_VIDEO_FILE_URI, uri)
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
        if (mediaFile.videoStream?.fullFeatured == false) {
            _framesToShowNumber.value = FramesToShow.FOUR
        }
        _audioStreamsLiveData.value = mediaFile.audioStreams
        _subtitleStreamsLiveData.value = mediaFile.subtitleStreams
        setupTabsAvailable(mediaFile)
        tryLoadVideoFrames(true)
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

    fun setFramesToShow(framesToShow: FramesToShow) {
        savedStateHandle.set(KEY_FRAMES_NUMBER, framesToShow.toString())
        _framesToShowNumber.value = framesToShow
        tryLoadVideoFrames(false)
    }

    private fun tryLoadVideoFrames(generateBackgroundColor: Boolean) {
        if (_basicVideoInfoLiveData.value != null) {
            LoadingTask(generateBackgroundColor).execute()
        }
    }

    private fun clearPendingUri() {
        pendingMediaFileUri = null
    }

    // Well, I'm not proud of using AsyncTask, but this app doesn't need more sophisticated things at all
    private inner class LoadingTask(private val generateBackgroundColor: Boolean) : AsyncTask<Unit, Unit, VideoProcessingResult>() {
        override fun onPreExecute() {
            _modalProgressLiveData.value = true
        }

        override fun doInBackground(vararg param: Unit?): VideoProcessingResult {
            val framesToShow = framesToShowNumber.value!!.value
            val basicInfo = basicVideoInfoLiveData.value!!

            val childFrameWidth = frameFullWidth / sqrt(framesToShow.toDouble()).toInt()
            val childFrameHeight = (childFrameWidth * basicInfo.frameHeight / basicInfo.frameWidth.toDouble()).toInt()

            val bitmaps = Array<Bitmap>(framesToShow) {
                Bitmap.createBitmap(childFrameWidth, childFrameHeight, Bitmap.Config.ARGB_8888)
            }

            mediaFile?.videoStream?.fillWithPreview(bitmaps)

            var backgroundColor: Int = Color.TRANSPARENT
            if (generateBackgroundColor) {
                val palette = Palette.from(bitmaps.first()).generate()
                // Pick the first swatch in this order that isn't null and use its color
                backgroundColor = listOf(
                        palette::getDarkMutedSwatch,
                        palette::getMutedSwatch,
                        palette::getDominantSwatch
                ).firstOrNull {
                    it() != null
                }?.invoke()?.rgb ?: backgroundColor
            }

            return VideoProcessingResult(bitmaps, backgroundColor)
        }

        override fun onPostExecute(result: VideoProcessingResult) {
            if (generateBackgroundColor) {
                _framesBackgroundLiveData.value = result.backgroundColor
            }

            val previousFrames = _framesLiveData.value
            _framesLiveData.value = result.frames
            previousFrames?.forEach { it.recycle() }

            _modalProgressLiveData.value = false
        }
    }

    private fun framesInitialValue(): FramesToShow {
        val savedFramesNumber = savedStateHandle.get<String?>(KEY_FRAMES_NUMBER)
        return if (savedFramesNumber != null) {
            FramesToShow.valueOf(savedFramesNumber)
        } else {
            FramesToShow.ONE
        }
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
        const val KEY_VIDEO_FILE_URI = "key_video_file_uri"
        const val KEY_FRAMES_NUMBER = "key_frames_number"
    }

    private class VideoProcessingResult(
            val frames: Array<Bitmap>,
            val backgroundColor: Int
    )
}

