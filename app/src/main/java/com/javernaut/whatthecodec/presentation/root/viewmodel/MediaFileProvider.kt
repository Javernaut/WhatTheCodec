package com.javernaut.whatthecodec.presentation.root.viewmodel

import com.javernaut.mediafile.MediaFile

interface MediaFileProvider {
    fun obtainMediaFile(argument: MediaFileArgument): MediaFile?
}
