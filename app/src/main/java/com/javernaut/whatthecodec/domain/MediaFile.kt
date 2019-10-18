package com.javernaut.whatthecodec.domain

import android.os.ParcelFileDescriptor

/**
 * A structure that has metadata of a video or audio file and its media streams.
 */
class MediaFile(
        val fileFormatName: String,
        val videoStream: VideoStream?,
        val audioStreams: List<AudioStream>,
        val subtitleStreams: List<SubtitleStream>,
        private val parcelFileDescriptor: ParcelFileDescriptor?
) {

    fun release() {
        videoStream?.release()
        parcelFileDescriptor?.close()
    }

}

