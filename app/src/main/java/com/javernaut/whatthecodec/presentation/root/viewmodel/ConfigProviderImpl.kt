package com.javernaut.whatthecodec.presentation.root.viewmodel

import android.content.Context
import android.net.Uri
import com.javernaut.whatthecodec.domain.VideoFileConfig
import com.javernaut.whatthecodec.domain.VideoFileConfigBuilder
import com.javernaut.whatthecodec.util.PathUtil
import java.io.FileNotFoundException

class ConfigProviderImpl(context: Context) : ConfigProvider {
    private val appContext = context.applicationContext

    override fun obtainConfig(uri: String): VideoFileConfig? {
        val androidUri = Uri.parse(uri)
        var config: VideoFileConfig? = null

        // First, try get a file:// path
        val path = PathUtil.getPath(appContext, androidUri)
        if (path != null) {
            config = VideoFileConfigBuilder().from(path).create()
        }

        // Second, try get a FileDescriptor.
        if (config == null) {
            try {
                val descriptor = appContext.contentResolver.openFileDescriptor(androidUri, "r")
                if (descriptor != null) {
                    config = VideoFileConfigBuilder().from(descriptor).create()
                }
            } catch (e: FileNotFoundException) {
            }
        }

        return config
    }

}