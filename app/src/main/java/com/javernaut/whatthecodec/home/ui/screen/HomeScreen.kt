package com.javernaut.whatthecodec.home.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.MimeTypeFilter
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.home.presentation.MediaFileArgument
import com.javernaut.whatthecodec.home.presentation.MediaFileViewModel
import com.javernaut.whatthecodec.home.presentation.MediaType
import com.javernaut.whatthecodec.util.TinyActivityCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

@Composable
fun HomeScreenActionView(
    contentUri: Uri,
    mimeType: String,
    onSettingsClicked: () -> Unit
) {
    val viewModel: MediaFileViewModel = hiltViewModel()

    if (TinyActivityCompat.needRequestReadStoragePermission(LocalContext.current)) {
        val launcher =
            rememberLauncherForActivityResult(contract = TinyActivityCompat.requestPermissionContract()) {
                if (it) {
                    val argument = MediaFileArgument(
                        contentUri, if (MimeTypeFilter.matches(mimeType, "audio/*")) {
                            MediaType.AUDIO
                        } else {
                            MediaType.VIDEO
                        }
                    )
                    viewModel.openMediaFile(argument)
                } else {
                    viewModel.onPermissionDenied()
                }
            }

        LaunchedEffect(contentUri) {
            TinyActivityCompat.requestReadStoragePermission(launcher)
        }
    }

    HomeScreen(
        viewModel = viewModel,
        onSettingsClicked = onSettingsClicked
    )
}

@Composable
fun HomeScreen(
    viewModel: MediaFileViewModel = hiltViewModel(),
    onSettingsClicked: () -> Unit
) {
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()

    val pickVideoFile = pickVideoFile(permissionDenied = viewModel::onPermissionDenied) {
        viewModel.openMediaFile(
            MediaFileArgument(it, MediaType.VIDEO)
        )
    }

    val pickAudioFile = pickAudioFile(permissionDenied = viewModel::onPermissionDenied) {
        viewModel.openMediaFile(
            MediaFileArgument(it, MediaType.AUDIO)
        )
    }

    if (screenState == null) {
        EmptyHomeScreen(
            pickVideoFile,
            pickAudioFile,
            onSettingsClicked,
            viewModel.screenMessage
        )
    } else {
        MainHomeScreen(
            screenState!!,
            pickVideoFile,
            pickAudioFile,
            onSettingsClicked,
            viewModel::copyToClipboard,
            viewModel.screenMessage
        )
    }
}

@Composable
fun HomeScreenSettingsAction(
    onSettingsClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onSettingsClicked,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Filled.Settings,
            contentDescription = stringResource(id = R.string.menu_settings),
        )
    }
}

@Composable
fun <T> ObserveAsEvents(flow: Flow<T>, onEvent: (T) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(flow, lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                flow.collect(onEvent)
            }
        }
    }
}
