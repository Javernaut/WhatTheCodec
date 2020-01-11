package com.javernaut.whatthecodec.domain

/**
 * Represents metadata of a video stream in a video file. Allows video frames reading.
 */
class VideoStream(
        val basicInfo: BasicStreamInfo,
        val bitRate: Long,
        val frameWidth: Int,
        val frameHeight: Int,
        // TODO move to MediaFile itself
        val fullFeatured: Boolean
)