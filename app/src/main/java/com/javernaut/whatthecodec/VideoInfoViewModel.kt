package com.javernaut.whatthecodec

import android.app.Activity
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.FileNotFoundException

class VideoInfoViewModel : ViewModel() {
    private val _basicInfoLiveData = MutableLiveData<BasicInfo>()
    private val _isFullFeaturedLiveData = MutableLiveData<Boolean>()
    private val _framesToShowNumber = MutableLiveData<FramesToShow>(FramesToShow.ONE)
    private val _videoFileConfigLiveData = MutableLiveData<VideoFileConfig?>()

    val basicInfoLiveData: LiveData<BasicInfo>
        get() = _basicInfoLiveData

    val isFullFeaturedLiveData: LiveData<Boolean>
        get() = _isFullFeaturedLiveData

    val framesToShowNumber: LiveData<FramesToShow>
        get() = _framesToShowNumber

    @Deprecated("We shouldn't cache video config object. We need to cache bitmaps from it and do only once")
    val videoFileConfigLiveData: LiveData<VideoFileConfig?>
        get() = _videoFileConfigLiveData

    // TODO get rid of an Activity as a parameter
    fun tryGetVideoConfig(activity: Activity, uri: Uri) {
        var videoFileConfig: VideoFileConfig? = null

        // First, try get a file:// path
        val path = PathUtil.getPath(activity, uri)
        if (path != null) {
            videoFileConfig = VideoFileConfig.create(path)
        }

        // Second, try get a FileDescriptor.
        if (videoFileConfig == null) {
            try {
                val descriptor = activity.contentResolver.openFileDescriptor(uri, "r")
                if (descriptor != null) {
                    videoFileConfig = VideoFileConfig.create(descriptor)
                }
            } catch (e: FileNotFoundException) {
            }
        }

        if (videoFileConfig != null) {
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
        }

        _videoFileConfigLiveData.value = videoFileConfig
    }

    fun setFramesToShow(framesToShow: FramesToShow) {
        _framesToShowNumber.value = framesToShow
    }
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