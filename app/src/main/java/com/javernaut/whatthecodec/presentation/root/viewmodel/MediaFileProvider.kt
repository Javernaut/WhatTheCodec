package com.javernaut.whatthecodec.presentation.root.viewmodel

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.javernaut.mediafile.MediaFile
import io.github.javernaut.mediafile.creator.MediaFileCreator
import javax.inject.Inject

class MediaFileProvider @Inject constructor(
    @ApplicationContext context: Context
) {
    private val mediaFileCreator = MediaFileCreator(context)

    fun obtainMediaFile(argument: MediaFileArgument): MediaFile? {
        return mediaFileCreator.createMediaFile(argument.uri, argument.type)
    }
}
