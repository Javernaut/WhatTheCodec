package com.javernaut.whatthecodec.home.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.content.MimeTypeFilter
import com.javernaut.whatthecodec.compose.theme.WhatTheCodecTheme
import com.javernaut.whatthecodec.home.presentation.MediaFileArgument
import com.javernaut.whatthecodec.home.presentation.MediaFileViewModel
import com.javernaut.whatthecodec.util.TinyActivityCompat
import dagger.hilt.android.AndroidEntryPoint
import io.github.javernaut.mediafile.creator.MediaType

@AndroidEntryPoint
class RootActivity : ComponentActivity() {

    private val mediaFileViewModel: MediaFileViewModel by viewModels()

    private val permissionRequestLauncher =
        registerForActivityResult(TinyActivityCompat.requestPermissionContract()) {
            if (it) {
                openMediaFile(
                    intent.data!!, if (MimeTypeFilter.matches(intent.type, MIME_TYPE_AUDIO)) {
                        MediaType.AUDIO
                    } else {
                        MediaType.VIDEO
                    }
                )
            } else {
                mediaFileViewModel.onPermissionDenied()
            }
            intent.data = null
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            WhatTheCodecTheme.Dynamic {
                WhatTheCodecApp(mediaFileViewModel)
            }
        }

        if (savedInstanceState == null) {
            onCheckForActionView()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        onCheckForActionView()
    }

    private fun onCheckForActionView() {
        if (Intent.ACTION_VIEW == intent.action && intent.data != null) {
            TinyActivityCompat.requestReadStoragePermission(permissionRequestLauncher)
        }
    }

    private fun openMediaFile(uri: Uri, mediaType: MediaType) {
        mediaFileViewModel.openMediaFile(MediaFileArgument(uri.toString(), mediaType))
    }

    companion object {
        private const val MIME_TYPE_AUDIO = "audio/*"
    }
}
