package com.javernaut.whatthecodec.domain

import android.os.ParcelFileDescriptor
import androidx.annotation.Keep

/**
 * Class that aggregates a creation process of a [VideoFileConfig] object. Certain private methods are
 * called from JNI layer.
 */
class VideoFileConfigBuilder {

    private var error = false

    private var parcelFileDescriptor: ParcelFileDescriptor? = null

    private var fileFormatName: String? = null

    private var videoStream: VideoStream? = null
    private var audioStreams = mutableListOf<AudioStream>()

    /**
     * Tries reading all metadata for a [VideoFileConfig] object from a file path.
     */
    fun from(filePath: String) = apply {
        nativeCreateFromPath(filePath)
    }

    /**
     * Tries reading all metadata for a [VideoFileConfig] object from a file descriptor. The file descriptor is saved and
     * closed when [VideoFileConfig.release] method is called.
     */
    fun from(descriptor: ParcelFileDescriptor) = apply {
        this.parcelFileDescriptor = descriptor
        nativeCreateFromFD(descriptor.fd)
    }

    /**
     * Combines all data read from FFmpeg into a [VideoFileConfig] object. If there was error during
     * metadata reading then null is returned.
     */
    fun create(): VideoFileConfig? {
        return if (!error) {
            VideoFileConfig(fileFormatName!!, videoStream!!, audioStreams, parcelFileDescriptor)
        } else {
            null
        }
    }

    @Keep
    /* Used from JNI */
    private fun onError() {
        this.error = true
    }

    @Keep
    /* Used from JNI */
    private fun onVideoConfigFound(fileFormatName: String) {
        this.fileFormatName = fileFormatName
    }

    @Keep
    /* Used from JNI */
    private fun onVideoStreamFound(frameWidth: Int, frameHeight: Int, codecName: String, nativePointer: Long) {
        if (videoStream == null) {
            videoStream = VideoStream(frameWidth,
                    frameHeight,
                    codecName,
                    parcelFileDescriptor == null,
                    nativePointer)
        }
    }

    @Keep
    /* Used from JNI */
    private fun onAudioStreamFound(
            index: Int,
            codecName: String,
            title: String?,
            language: String?,
            bitRate: Long,
            sampleFormat: String?,
            sampleRate: Int,
            channels: Int,
            channelLayout: String?,
            disposition: Int) {
        audioStreams.add(
                AudioStream(index, codecName, title, language, bitRate, sampleFormat, sampleRate, channels, channelLayout, disposition)
        )
    }

    private external fun nativeCreateFromFD(fileDescriptor: Int)

    private external fun nativeCreateFromPath(filePath: String)

    init {
        // The order of importing is mandatory, because otherwise the app will crash on Android API 16 and 17.
        // See: https://android.googlesource.com/platform/bionic/+/master/android-changes-for-ndk-developers.md#changes-to-library-dependency-resolution
        System.loadLibrary("avutil")
        System.loadLibrary("avcodec")
        System.loadLibrary("avformat")
        System.loadLibrary("swscale")
        System.loadLibrary("video-config")
    }
}