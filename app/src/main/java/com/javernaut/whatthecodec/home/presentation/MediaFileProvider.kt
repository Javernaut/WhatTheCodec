package com.javernaut.whatthecodec.home.presentation

import androidx.core.net.toUri
import io.github.javernaut.mediafile.MediaFile
import io.github.javernaut.mediafile.factory.MediaFileFactory
import io.github.javernaut.mediafile.factory.MediaSource
import javax.inject.Inject

class MediaFileProvider @Inject constructor() {
    fun obtainMediaFile(argument: MediaFileArgument): MediaFile? {
        return MediaFileFactory.create(
            // Limiting the file reading to content://
            MediaSource.Content(argument.uri.toUri()), argument.type
        )
    }
}
