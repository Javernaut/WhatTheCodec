package com.javernaut.mediafile.creator

enum class MediaType(val mediaStreamsMask: Int) {
    VIDEO(STREAM_VIDEO or STREAM_AUDIO or STREAM_SUBTITLE),
    AUDIO(STREAM_AUDIO);
}

private const val STREAM_VIDEO = 1
private const val STREAM_AUDIO = 1 shl 1
private const val STREAM_SUBTITLE = 1 shl 2
