package com.javernaut.whatthecodec.presentation.root.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.core.content.MimeTypeFilter
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.compose.theme.WhatTheCodecTheme
import com.javernaut.whatthecodec.presentation.root.viewmodel.MediaFileArgument
import com.javernaut.whatthecodec.presentation.root.viewmodel.MediaFileViewModel
import com.javernaut.whatthecodec.presentation.root.viewmodel.MediaFileViewModelFactory
import com.javernaut.whatthecodec.presentation.settings.SettingsActivity
import com.javernaut.whatthecodec.presentation.video.getDesiredFrameWidth
import com.javernaut.whatthecodec.util.TinyActivityCompat
import io.github.javernaut.mediafile.creator.MediaType

class RootActivity : AppCompatActivity() {

    private val mediaFileViewModel by viewModels<MediaFileViewModel> {
        MediaFileViewModelFactory(this, getDesiredFrameWidth(this))
    }

    private var intentActionViewConsumed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WhatTheCodecTheme {
                val screenState by mediaFileViewModel.screenState.observeAsState()
                if (screenState == null) {
                    EmptyScreen(::onPickVideoClicked, ::onPickAudioClicked) {
                        CommonMenuItems()
                    }
                } else {
                    MainScreen(screenState!!) {
                        SelectFileMenuItems()
                        CommonMenuItems()
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

    @Composable
    private fun SelectFileMenuItems() {
        IconButton(onClick = ::onPickVideoClicked) {
            Icon(
                Icons.Filled.Videocam,
                contentDescription = stringResource(id = R.string.menu_pick_video),
            )
        }
        IconButton(onClick = ::onPickAudioClicked) {
            Icon(
                Icons.Filled.MusicNote,
                contentDescription = stringResource(id = R.string.menu_pick_audio),
            )
        }
    }

    @Composable
    private fun CommonMenuItems() {
        val expanded = remember { mutableStateOf(false) }
        IconButton(onClick = { expanded.value = true }) {
            Icon(
                Icons.Filled.MoreVert,
                contentDescription = stringResource(id = R.string.menu_more),
            )
        }
        MoreSettingsDropdown(expanded)
    }

    @Composable
    private fun MoreSettingsDropdown(expanded: MutableState<Boolean>) {
        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            DropdownMenuItem(onClick = {
                SettingsActivity.start(this@RootActivity)
                expanded.value = false
            }) {
                Text(stringResource(id = R.string.menu_settings))
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
