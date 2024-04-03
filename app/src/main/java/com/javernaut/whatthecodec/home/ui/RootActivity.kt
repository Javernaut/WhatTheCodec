package com.javernaut.whatthecodec.home.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.core.content.MimeTypeFilter
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.compose.theme.WhatTheCodecTheme
import com.javernaut.whatthecodec.home.presentation.MediaFileArgument
import com.javernaut.whatthecodec.home.presentation.MediaFileViewModel
import com.javernaut.whatthecodec.settings.SettingsActivity
import com.javernaut.whatthecodec.util.TinyActivityCompat
import dagger.hilt.android.AndroidEntryPoint
import io.github.javernaut.mediafile.creator.MediaType

@AndroidEntryPoint
class RootActivity : AppCompatActivity() {

    private val mediaFileViewModel: MediaFileViewModel by viewModels()

    private var intentActionViewConsumed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            WhatTheCodecTheme {
                val screenState by mediaFileViewModel.screenState.observeAsState()
                if (screenState == null) {
                    EmptyScreen(::onPickVideoClicked, ::onPickAudioClicked) {
                        SettingsActivity.start(this@RootActivity)
                    }
                } else {
                    MainScreen(screenState!!, ::onPickVideoClicked, ::onPickAudioClicked) {
                        SettingsActivity.start(this@RootActivity)
                    }
                }
            }
        }

        mediaFileViewModel.errorMessageLiveEvent.observe(this) {
            if (it) {
                toast(R.string.message_couldnt_open_file)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_PICK_VIDEO || requestCode == REQUEST_CODE_PICK_AUDIO) {
            if (resultCode == RESULT_OK && data?.data != null) {
                openMediaFile(
                    data.data!!, if (requestCode == REQUEST_CODE_PICK_VIDEO) {
                        MediaType.VIDEO
                    } else {
                        MediaType.AUDIO
                    }
                )
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_PERMISSION_ACTION_VIEW,
            REQUEST_CODE_PERMISSION_PICK_VIDEO,
            REQUEST_CODE_PERMISSION_PICK_AUDIO -> {
                if (TinyActivityCompat.wasReadStoragePermissionGranted(permissions, grantResults)) {
                    when (requestCode) {
                        REQUEST_CODE_PERMISSION_ACTION_VIEW -> actualDisplayFileFromActionView()
                        REQUEST_CODE_PERMISSION_PICK_VIDEO -> actualPickVideoFile()
                        else -> actualPickAudioFile()
                    }
                } else {
                    toast(R.string.message_permission_denied)
                }
            }

            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    private fun onPickVideoClicked() {
        checkPermissionAndTryOpenMedia(REQUEST_CODE_PERMISSION_PICK_VIDEO) {
            actualPickVideoFile()
        }
    }

    private fun onPickAudioClicked() {
        checkPermissionAndTryOpenMedia(REQUEST_CODE_PERMISSION_PICK_AUDIO) {
            actualPickAudioFile()
        }
    }

    private fun onCheckForActionView() {
        if (Intent.ACTION_VIEW == intent.action && intent.data != null) {
            checkPermissionAndTryOpenMedia(REQUEST_CODE_PERMISSION_ACTION_VIEW) {
                actualDisplayFileFromActionView()
            }
        }
    }

    private inline fun checkPermissionAndTryOpenMedia(requestCode: Int, actualAction: () -> Unit) {
        if (TinyActivityCompat.needRequestReadStoragePermission(this)) {
            TinyActivityCompat.requestReadStoragePermission(this, requestCode)
        } else {
            actualAction()
        }
    }

    private fun actualPickVideoFile() {
        startActivityForMediaFile(MIME_TYPE_VIDEO, REQUEST_CODE_PICK_VIDEO)
    }

    private fun actualPickAudioFile() {
        startActivityForMediaFile(MIME_TYPE_AUDIO, REQUEST_CODE_PICK_AUDIO)
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

    private fun startActivityForMediaFile(type: String, requestCode: Int) {
        startActivityForResult(
            Intent(Intent.ACTION_GET_CONTENT)
                .setType(type)
                .putExtra(Intent.EXTRA_LOCAL_ONLY, true)
                .addCategory(Intent.CATEGORY_OPENABLE),
            requestCode
        )
    }

    private fun openMediaFile(uri: Uri, mediaType: MediaType) {
        mediaFileViewModel.openMediaFile(MediaFileArgument(uri.toString(), mediaType))
    }

    private fun toast(msg: Int) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val REQUEST_CODE_PICK_VIDEO = 41
        private const val REQUEST_CODE_PICK_AUDIO = 42
        private const val REQUEST_CODE_PERMISSION_ACTION_VIEW = 43
        private const val REQUEST_CODE_PERMISSION_PICK_VIDEO = 44
        private const val REQUEST_CODE_PERMISSION_PICK_AUDIO = 45

        private const val EXTRA_INTENT_ACTION_VIEW_CONSUMED = "extra_intent_action_view_consumed"

        private const val MIME_TYPE_VIDEO = "video/*"
        private const val MIME_TYPE_AUDIO = "audio/*"
    }
}
