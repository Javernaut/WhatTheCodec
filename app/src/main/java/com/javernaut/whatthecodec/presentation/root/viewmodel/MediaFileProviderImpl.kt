package com.javernaut.whatthecodec.presentation.root.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import com.javernaut.mediafile.MediaFile
import com.javernaut.mediafile.MediaFileBuilder
import com.javernaut.whatthecodec.util.PathUtil
import java.io.FileNotFoundException

class MediaFileProviderImpl(context: Context) : MediaFileProvider {
    private val appContext = context.applicationContext

    override fun obtainMediaFile(argument: MediaFileArgument): MediaFile? {
        val androidUri = Uri.parse(argument.uri)
        var config: MediaFile? = null

        // First, try get a file:// path
        val path = PathUtil.getPath(appContext, androidUri)
        if (path != null) {
            config = MediaFileBuilder(argument.type).from(path).create()
        }

        // Second, try get a FileDescriptor.
        if (config == null) {
            try {
                val descriptor = appContext.contentResolver.openFileDescriptor(androidUri, "r")
                if (descriptor != null) {
                    config = MediaFileBuilder(argument.type).from(descriptor).create()
                }
            } catch (e: FileNotFoundException) {
                Log.w("obtainMediaFile() error", e)
            }
        }

        return config
    }

}
