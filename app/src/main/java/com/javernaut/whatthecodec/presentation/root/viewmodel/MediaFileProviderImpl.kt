package com.javernaut.whatthecodec.presentation.root.viewmodel

import android.content.Context
import android.net.Uri
import com.javernaut.whatthecodec.domain.MediaFile
import com.javernaut.whatthecodec.domain.MediaFileBuilder
import com.javernaut.whatthecodec.util.PathUtil
import java.io.FileNotFoundException

class MediaFileProviderImpl(context: Context) : MediaFileProvider {
    private val appContext = context.applicationContext

    override fun obtainMediaFile(uri: String): MediaFile? {
        val androidUri = Uri.parse(uri)
        var config: MediaFile? = null

        // First, try get a file:// path
        val path = PathUtil.getPath(appContext, androidUri)
        if (path != null) {
            config = MediaFileBuilder().from(path).create()
        }

        // Second, try get a FileDescriptor.
        if (config == null) {
            try {
                val descriptor = appContext.contentResolver.openFileDescriptor(androidUri, "r")
                if (descriptor != null) {
                    config = MediaFileBuilder().from(descriptor).create()
                }
            } catch (e: FileNotFoundException) {
            }
        }

        return config
    }

}