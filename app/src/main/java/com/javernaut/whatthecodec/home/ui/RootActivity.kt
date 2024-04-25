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
import com.javernaut.whatthecodec.home.ui.screen.HomeScreen
import com.javernaut.whatthecodec.settings.SettingsActivity
import com.javernaut.whatthecodec.util.TinyActivityCompat
import dagger.hilt.android.AndroidEntryPoint
import io.github.javernaut.mediafile.creator.MediaType

@AndroidEntryPoint
class RootActivity : ComponentActivity() {

    private val mediaFileViewModel: MediaFileViewModel by viewModels()

    private var intentActionViewConsumed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            WhatTheCodecTheme.Dynamic {
                HomeScreen(
                    mediaFileViewModel,
                    ::onSettingsClicked
                )
            }
        }

        intentActionViewConsumed =
            savedInstanceState?.getBoolean(EXTRA_INTENT_ACTION_VIEW_CONSUMED) == true
        if (!intentActionViewConsumed) {
            onCheckForActionView()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        intentActionViewConsumed = false
        onCheckForActionView()
    }

    override fun onResume() {
        super.onResume()
        mediaFileViewModel.applyPendingMediaFileIfNeeded()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(EXTRA_INTENT_ACTION_VIEW_CONSUMED, intentActionViewConsumed)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_PERMISSION_ACTION_VIEW -> {
                if (TinyActivityCompat.wasReadStoragePermissionGranted(permissions, grantResults)) {
                    when (requestCode) {
                        REQUEST_CODE_PERMISSION_ACTION_VIEW -> actualDisplayFileFromActionView()
                    }
                } else {
                    mediaFileViewModel.onPermissionDenied()
                }
            }

            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    private fun onCheckForActionView() {
        if (Intent.ACTION_VIEW == intent.action && intent.data != null) {
            checkPermissionAndTryOpenMedia(REQUEST_CODE_PERMISSION_ACTION_VIEW) {
                actualDisplayFileFromActionView()
            }
        }
    }

    private fun onSettingsClicked() {
        SettingsActivity.start(this@RootActivity)
    }

    private inline fun checkPermissionAndTryOpenMedia(requestCode: Int, actualAction: () -> Unit) {
        if (TinyActivityCompat.needRequestReadStoragePermission(this)) {
            TinyActivityCompat.requestReadStoragePermission(this, requestCode)
        } else {
            actualAction()
        }
    }

    private fun actualDisplayFileFromActionView() {
        intentActionViewConsumed = true
        openMediaFile(
            intent.data!!, if (MimeTypeFilter.matches(intent.type, MIME_TYPE_AUDIO)) {
                MediaType.AUDIO
            } else {
                MediaType.VIDEO
            }
        )
    }

    private fun openMediaFile(uri: Uri, mediaType: MediaType) {
        mediaFileViewModel.openMediaFile(MediaFileArgument(uri.toString(), mediaType))
    }

    companion object {
        private const val REQUEST_CODE_PERMISSION_ACTION_VIEW = 43

        private const val EXTRA_INTENT_ACTION_VIEW_CONSUMED = "extra_intent_action_view_consumed"

        private const val MIME_TYPE_AUDIO = "audio/*"
    }
}
