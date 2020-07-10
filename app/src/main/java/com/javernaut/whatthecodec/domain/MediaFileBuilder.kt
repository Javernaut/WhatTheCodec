package com.javernaut.whatthecodec.domain

import android.content.res.AssetFileDescriptor
import android.os.ParcelFileDescriptor
import androidx.annotation.Keep

/**
 * Class that aggregates a creation process of a [MediaFile] object. Certain private methods are
 * called from JNI layer.
 */
class MediaFileBuilder(private val mediaType: MediaType) {

    private var error = false

    private var parcelFileDescriptor: ParcelFileDescriptor? = null

    private var fileFormatName: String? = null

    private var videoStream: VideoStream? = null
    private var frameLoaderContextHandle: Long? = null
    private var audioStreams = mutableListOf<AudioStream>()
    private var subtitleStream = mutableListOf<SubtitleStream>()

    /**
     * Tries reading all metadata for a [MediaFile] object from a file path.
     */
    fun from(filePath: String) = apply {
        nativeCreateFromPath(filePath, mediaType.mediaStreamsMask)
    }

    /**
     * Tries reading all metadata for a [MediaFile] object from a file descriptor. The file descriptor is saved and
     * closed when [MediaFile.release] method is called.
     */
    fun from(descriptor: ParcelFileDescriptor) = apply {
        this.parcelFileDescriptor = descriptor
        nativeCreateFromFD(descriptor.fd, mediaType.mediaStreamsMask)
    }

    /**
     * Tries reading all metadata for a [MediaFile] object from a file from app's assets catalog. The file descriptor is saved and
     * closed when [MediaFile.release] method is called.
     *
     * @param shortFormatName a short name of a file format, as there is a problem in probing certain formats (like mkv).
     * If a file comes from assets catalog, then its format should be known to a developer.
     * All default formats are listed here: https://ffmpeg.org/ffmpeg-formats.html
     */
    fun from(assetFileDescriptor: AssetFileDescriptor, shortFormatName: String) = apply {
        val descriptor = assetFileDescriptor.parcelFileDescriptor
        this.parcelFileDescriptor = descriptor
        nativeCreateFromAssetFD(descriptor.fd, assetFileDescriptor.startOffset, shortFormatName, mediaType.mediaStreamsMask)
    }

    /**
     * Combines all data read from FFmpeg into a [MediaFile] object. If there was error during
     * metadata reading then null is returned.
     */
    fun create(): MediaFile? {
        return if (!error) {
            MediaFile(fileFormatName!!, videoStream, audioStreams, subtitleStream, parcelFileDescriptor, frameLoaderContextHandle)
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
    private fun onMediaFileFound(fileFormatName: String) {
        this.fileFormatName = fileFormatName
    }

    @Keep
    /* Used from JNI */
    private fun onVideoStreamFound(
            basicStreamInfo: BasicStreamInfo,
            bitRate: Long,
            frameWidth: Int,
            frameHeight: Int,
            frameLoaderContext: Long) {
        if (videoStream == null) {
            videoStream = VideoStream(
                    basicStreamInfo,
                    bitRate,
                    frameWidth,
                    frameHeight)
            if (frameLoaderContext != -1L) {
                frameLoaderContextHandle = frameLoaderContext
            }
        }
    }

    @Keep
    /* Used from JNI */
    private fun onAudioStreamFound(
            basicStreamInfo: BasicStreamInfo,
            bitRate: Long,
            sampleFormat: String?,
            sampleRate: Int,
            channels: Int,
            channelLayout: String?) {
        audioStreams.add(
                AudioStream(basicStreamInfo, bitRate, sampleFormat, sampleRate, channels, channelLayout)
        )
    }

    @Keep
    /* Used from JNI */
    private fun onSubtitleStreamFound(basicStreamInfo: BasicStreamInfo) {
        subtitleStream.add(
                SubtitleStream(basicStreamInfo)
        )
    }

    @Keep
    /* Used from JNI */
    private fun createBasicInfo(index: Int,
                                title: String?,
                                codecName: String,
                                language: String?,
                                disposition: Int) = BasicStreamInfo(index, title, codecName, language, disposition)

    private external fun nativeCreateFromFD(fileDescriptor: Int, mediaStreamsMask: Int)

    private external fun nativeCreateFromAssetFD(assetFileDescriptor: Int, startOffset: Long, shortFormatName: String, mediaStreamsMask: Int)

    private external fun nativeCreateFromPath(filePath: String, mediaStreamsMask: Int)

    init {
        // The order of importing is mandatory, because otherwise the app will crash on Android API 16 and 17.
        // See: https://android.googlesource.com/platform/bionic/+/master/android-changes-for-ndk-developers.md#changes-to-library-dependency-resolution
        System.loadLibrary("avutil")
        System.loadLibrary("avcodec")
        System.loadLibrary("avformat")
        System.loadLibrary("swscale")
        System.loadLibrary("media-file")
    }
}
