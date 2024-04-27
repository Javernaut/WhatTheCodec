package com.javernaut.whatthecodec.home.ui.screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import com.javernaut.whatthecodec.util.TinyActivityCompat

@Composable
fun pickAudioFile(
    permissionDenied: () -> Unit,
    filePicked: (Uri) -> Unit
) =
    pickMediaFile(
        mimeType = "audio/*",
        permissionDenied = permissionDenied,
        filePicked = filePicked
    )

@Composable
fun pickVideoFile(
    permissionDenied: () -> Unit,
    filePicked: (Uri) -> Unit
) =
    pickMediaFile(
        mimeType = "video/*",
        permissionDenied = permissionDenied,
        filePicked = filePicked
    )

@Composable
private fun pickMediaFile(
    mimeType: String,
    permissionDenied: () -> Unit,
    filePicked: (Uri) -> Unit
): () -> Unit {
    val currentFilePicked by rememberUpdatedState(filePicked)

    val filePickerLauncher =
        rememberLauncherForActivityResult(contract = GetLocalContentContract) { uri ->
            uri?.let {
                currentFilePicked(it)
            }
        }

    val pickMediaFile = {
        filePickerLauncher.launch(mimeType)
    }

    return if (!TinyActivityCompat.needRequestReadStoragePermission(LocalContext.current)) {
        pickMediaFile
    } else {
        pickFileWithPermission(pickFile = pickMediaFile, permissionDenied = permissionDenied)
    }
}

@Composable
private fun pickFileWithPermission(
    pickFile: () -> Unit,
    permissionDenied: () -> Unit
): () -> Unit {
    val currentPermissionDenied by rememberUpdatedState(permissionDenied)
    val permissionRequestLauncher =
        rememberLauncherForActivityResult(TinyActivityCompat.requestPermissionContract()) {
            if (it) {
                pickFile()
            } else {
                currentPermissionDenied()
            }
        }

    val requestPermission by rememberUpdatedState {
        TinyActivityCompat.requestReadStoragePermission(permissionRequestLauncher)
    }

    return requestPermission
}


private val GetLocalContentContract =
    object : ActivityResultContracts.GetContent() {
        override fun createIntent(context: Context, input: String) =
            super.createIntent(context, input)
                .putExtra(Intent.EXTRA_LOCAL_ONLY, true)
    }
