package com.javernaut.whatthecodec.presentation.viewmodel

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.palette.graphics.Palette
import com.javernaut.whatthecodec.util.PathUtil
import com.javernaut.whatthecodec.domain.VideoFileConfig
import java.io.FileNotFoundException
import kotlin.math.sqrt

class VideoInfoViewModel(private val frameFullWidth: Int) : ViewModel() {

    private var videoFileConfig: VideoFileConfig? = null

    private val _basicInfoLiveData = MutableLiveData<BasicInfo>()
    private val _isFullFeaturedLiveData = MutableLiveData<Boolean>()
    private val _framesToShowNumber = MutableLiveData<FramesToShow>(FramesToShow.ONE)
    private val _videoFileConfigLiveData = MutableLiveData<VideoFileConfig?>()
    private val _modalProgressLiveData = MutableLiveData<Boolean>()
    private val _framesLiveData = MutableLiveData<Array<Bitmap>>()
    private val _framesBackgroundLiveData = MutableLiveData<Int>(Color.TRANSPARENT)

    val basicInfoLiveData: LiveData<BasicInfo>
        get() = _basicInfoLiveData

    val isFullFeaturedLiveData: LiveData<Boolean>
        get() = _isFullFeaturedLiveData

    val framesToShowNumber: LiveData<FramesToShow>
        get() = _framesToShowNumber

    @Deprecated("We shouldn't cache video config object. We need to cache bitmaps from it and do only once")
    val videoFileConfigLiveData: LiveData<VideoFileConfig?>
        get() = _videoFileConfigLiveData

    val modalProgressLiveData: LiveData<Boolean>
        get() = _modalProgressLiveData

    val framesLiveData: LiveData<Array<Bitmap>>
        get() = _framesLiveData

    val framesBackgroundLiveData: LiveData<Int>
        get() = _framesBackgroundLiveData

    // TODO get rid of an Activity as a parameter
    fun tryGetVideoConfig(activity: Activity, uri: Uri) {
        var config: VideoFileConfig? = null

        // First, try get a file:// path
        val path = PathUtil.getPath(activity, uri)
        if (path != null) {
            config = VideoFileConfig.create(path)
        }

        // Second, try get a FileDescriptor.
        if (config == null) {
            try {
                val descriptor = activity.contentResolver.openFileDescriptor(uri, "r")
                if (descriptor != null) {
                    config = VideoFileConfig.create(descriptor)
                }
            } catch (e: FileNotFoundException) {
            }
        }

        videoFileConfig = config
        if (config != null) {
            applyVideoConfig(config)
        } else {
            _videoFileConfigLiveData.value = config
        }
    }

    private fun applyVideoConfig(videoFileConfig: VideoFileConfig) {
        _basicInfoLiveData.value = BasicInfo(
                videoFileConfig.fileFormat,
                videoFileConfig.codecName,
                videoFileConfig.width,
                videoFileConfig.height
        )
        _isFullFeaturedLiveData.value = videoFileConfig.fullFeatured
        if (!videoFileConfig.fullFeatured) {
            _framesToShowNumber.value = FramesToShow.FOUR
        }
        LoadingTask(true).execute()
    }

    fun setFramesToShow(framesToShow: FramesToShow) {
        _framesToShowNumber.value = framesToShow
        // Temporary fix for crash due to RadioGroup state restoring logic
        if (_basicInfoLiveData.value != null) {
            LoadingTask(false).execute()
        }
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

            // TODO Need to recycle previously created bitmaps
            val bitmaps = Array<Bitmap>(framesToShow) {
                Bitmap.createBitmap(childFrameWidth, childFrameHeight, Bitmap.Config.ARGB_8888)
            }

            videoFileConfig?.fillWithPreview(bitmaps)

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
            _framesLiveData.value = result.frames
            _modalProgressLiveData.value = false
        }
    }

    private data class VideoProcessingResult(
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