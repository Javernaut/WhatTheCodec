package com.javernaut.whatthecodec.presentation.root.viewmodel

import android.content.Context
import io.github.javernaut.mediafile.MediaFile
import io.github.javernaut.mediafile.creator.MediaFileCreator

class MediaFileProvider(context: Context) {
    private val mediaFileCreator = MediaFileCreator(context)

    fun obtainMediaFile(argument: MediaFileArgument): MediaFile? {
        return mediaFileCreator.createMediaFile(argument.uri, argument.type)
    }
}
