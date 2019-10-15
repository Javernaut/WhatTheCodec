package com.javernaut.whatthecodec.presentation.viewmodel

import android.graphics.Bitmap
import android.graphics.Color
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.palette.graphics.Palette
import com.hadilq.liveevent.LiveEvent
import com.javernaut.whatthecodec.domain.VideoFileConfig
import kotlin.math.sqrt

class VideoInfoViewModel(private val frameFullWidth: Int,
                         private val configProvider: ConfigProvider,
                         private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private var pendingVideoFileUri: String? = null

    private var videoFileConfig: VideoFileConfig? = null

    private val _basicInfoLiveData = MutableLiveData<BasicInfo>()
    private val _isFullFeaturedLiveData = MutableLiveData<Boolean>()
    private val _framesToShowNumber = MutableLiveData<FramesToShow>(framesInitialValue())
    private val _modalProgressLiveData = MutableLiveData<Boolean>()
    private val _framesLiveData = MutableLiveData<Array<Bitmap>>()
    private val _framesBackgroundLiveData = MutableLiveData<Int>(Color.TRANSPARENT)
    private val _errorMessageLiveEvent = LiveEvent<Boolean>()

    init {
        pendingVideoFileUri = savedStateHandle[KEY_VIDEO_FILE_URI]
    }

    val basicInfoLiveData: LiveData<BasicInfo>
        get() = _basicInfoLiveData

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

    override fun onCleared() {
        videoFileConfig?.release()
    }

    fun applyPendingVideoConfigIfNeeded() {
        if (pendingVideoFileUri != null) {
            openVideoConfig(pendingVideoFileUri!!)
        }
    }

    fun openVideoConfig(uri: String) {
        clearPendingUri()

        val newVideoConfig = configProvider.obtainConfig(uri)
        if (newVideoConfig != null) {
            savedStateHandle.set(KEY_VIDEO_FILE_URI, uri)
            videoFileConfig?.release()
            videoFileConfig = newVideoConfig
            applyVideoConfig(newVideoConfig)
        } else {
            _errorMessageLiveEvent.value = true
        }
    }

    private fun applyVideoConfig(videoFileConfig: VideoFileConfig) {
        _basicInfoLiveData.value = BasicInfo(
                videoFileConfig.fileFormatName,
                videoFileConfig.videoStream.codecName,
                videoFileConfig.videoStream.frameWidth,
                videoFileConfig.videoStream.frameHeight
        )
        _isFullFeaturedLiveData.value = videoFileConfig.videoStream.fullFeatured
        if (!videoFileConfig.videoStream.fullFeatured) {
            _framesToShowNumber.value = FramesToShow.FOUR
        }
        LoadingTask(true).execute()
    }

    fun setFramesToShow(framesToShow: FramesToShow) {
        savedStateHandle.set(KEY_FRAMES_NUMBER, framesToShow.toString())
        _framesToShowNumber.value = framesToShow
        // Temporary fix for crash due to RadioGroup state restoring logic
        if (_basicInfoLiveData.value != null) {
            LoadingTask(false).execute()
        }
    }

    private fun clearPendingUri() {
        pendingVideoFileUri = null
    }

    // Well, I'm not proud of using AsyncTask, but this app doesn't need more sophisticated things at all
    private inner class LoadingTask(private val generateBackgroundColor: Boolean) : AsyncTask<Unit, Unit, VideoProcessingResult>() {
        override fun onPreExecute() {
            _modalProgressLiveData.value = true
        }

        override fun doInBackground(vararg param: Unit?): VideoProcessingResult {
            val framesToShow = framesToShowNumber.value!!.value
            val basicInfo = basicInfoLiveData.value!!

            val childFrameWidth = frameFullWidth / sqrt(framesToShow.toDouble()).toInt()
            val childFrameHeight = (childFrameWidth * basicInfo.frameHeight / basicInfo.frameWidth.toDouble()).toInt()

            val bitmaps = Array<Bitmap>(framesToShow) {
                Bitmap.createBitmap(childFrameWidth, childFrameHeight, Bitmap.Config.ARGB_8888)
            }

            videoFileConfig?.videoStream?.fillWithPreview(bitmaps)

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

    companion object {
        const val KEY_VIDEO_FILE_URI = "key_video_file_uri"
        const val KEY_FRAMES_NUMBER = "key_frames_number"
    }

    private class VideoProcessingResult(
            val frames: Array<Bitmap>,
            val backgroundColor: Int
    )
}

enum class FramesToShow(val value: Int) {
    ONE(1),
    FOUR(4),
    NINE(9)
}

data class BasicInfo(
        val fileFormat: String,
        val codecName: String,
        val frameWidth: Int,
        val frameHeight: Int
)