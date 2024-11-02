package com.javernaut.whatthecodec.home.presentation

import androidx.core.net.toUri
import io.github.javernaut.mediafile.factory.MediaFileContext
import io.github.javernaut.mediafile.factory.MediaFileFactory
import io.github.javernaut.mediafile.factory.Request
import javax.inject.Inject

class MediaFileProvider @Inject constructor() {
    fun obtainMediaFile(argument: MediaFileArgument): MediaFileContext? {
        return MediaFileFactory.create(
            Request.Content(argument.uri.toUri()), argument.type
        )
    }
}
