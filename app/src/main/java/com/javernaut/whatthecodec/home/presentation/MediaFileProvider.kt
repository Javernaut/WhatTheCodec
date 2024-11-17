package com.javernaut.whatthecodec.home.presentation

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.javernaut.mediafile.MediaFile
import io.github.javernaut.mediafile.MediaFileFactory
import io.github.javernaut.mediafile.MediaSource
import javax.inject.Inject

class MediaFileProvider @Inject constructor(
    @ApplicationContext context: Context
) {
    private val mediaFileFactory = MediaFileFactory.getDefault(context)

    fun obtainMediaFile(argument: MediaFileArgument): MediaFile? {
        return mediaFileFactory.create(
            MediaSource.Content(argument.uri), argument.type
        )
    }
}
