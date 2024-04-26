package com.javernaut.whatthecodec.home.ui.screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
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
        contract = GetLocalContentContract,
        contractArgument = "audio/*",
        permissionDenied = permissionDenied, filePicked = filePicked
    )

@Composable
fun pickVideoFile(
    permissionDenied: () -> Unit,
    filePicked: (Uri) -> Unit
) =
    pickMediaFile(
        // TODO Check ActivityResultContracts.PickVisualMedia
        contract = GetLocalContentContract,
        contractArgument = "video/*",
        permissionDenied = permissionDenied, filePicked = filePicked
    )

@Composable
private fun <I> pickMediaFile(
    contract: ActivityResultContract<I, Uri?>,
    contractArgument: I,
    permissionDenied: () -> Unit,
    filePicked: (Uri) -> Unit
): () -> Unit {
    val currentFilePicked by rememberUpdatedState(filePicked)

    val audioPickerLauncher =
        rememberLauncherForActivityResult(contract = contract) { uri ->
            uri?.let {
                currentFilePicked(it)
            }
        }

    val pickAudio = {
        audioPickerLauncher.launch(contractArgument)
    }

    return if (!TinyActivityCompat.needRequestReadStoragePermission(LocalContext.current)) {
        pickAudio
    } else {
        pickFileWithPermission(pickFile = pickAudio, permissionDenied = permissionDenied)
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
            // TODO Consider ignoring the denied permission and picking the file anyway
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
