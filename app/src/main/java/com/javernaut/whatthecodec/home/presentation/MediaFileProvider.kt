package com.javernaut.whatthecodec.home.presentation

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.javernaut.mediafile.MediaFileFactory
import io.github.javernaut.mediafile.MediaSource
import javax.inject.Inject

class MediaFileProvider @Inject constructor(
    @ApplicationContext context: Context
) {
    private val mediaFileFactory = MediaFileFactory.getDefault(context)

    fun obtainMediaFile(uri: Uri) =
        mediaFileFactory.create(
            MediaSource.Content(uri)
        )
}
