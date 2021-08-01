package com.javernaut.mediafile

/**
 * Represents metadata of a video stream in a video file. Allows video frames reading.
 */
class VideoStream(
    val basicInfo: BasicStreamInfo,
    val bitRate: BitRate,
    val frameWidth: Int,
    val frameHeight: Int
)
