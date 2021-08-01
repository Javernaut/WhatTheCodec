package com.javernaut.mediafile.creator

import android.content.Context
import android.net.Uri
import android.util.Log
import com.javernaut.mediafile.MediaFile
import java.io.FileNotFoundException

class MediaFileCreator(context: Context) {
    private val appContext = context.applicationContext

    fun createMediaFile(uri: String, type: MediaType): MediaFile? {
        val androidUri = Uri.parse(uri)
        var config: MediaFile? = null

        // First, try get a file:// path
        val path = PathUtil.getPath(appContext, androidUri)
        if (path != null) {
            config = MediaFileBuilder(type).from(path).create()
        }

        // Second, try get a FileDescriptor.
        if (config == null) {
            try {
                val descriptor = appContext.contentResolver.openFileDescriptor(androidUri, "r")
                if (descriptor != null) {
                    config = MediaFileBuilder(type).from(descriptor).create()
                }
            } catch (e: FileNotFoundException) {
                Log.w("createMediaFile() error", e)
            }
        }

        return config
    }
}
