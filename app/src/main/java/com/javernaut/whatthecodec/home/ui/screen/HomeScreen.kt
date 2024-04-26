package com.javernaut.whatthecodec.home.ui.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.home.presentation.MediaFileArgument
import com.javernaut.whatthecodec.home.presentation.MediaFileViewModel
import io.github.javernaut.mediafile.creator.MediaType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

@Composable
fun HomeScreen(
    viewModel: MediaFileViewModel,
    onSettingsClicked: () -> Unit
) {
    val screenState by viewModel.screenState.collectAsState()

    val pickVideoFile = pickVideoFile(permissionDenied = viewModel::onPermissionDenied) {
        viewModel.openMediaFile(
            MediaFileArgument(it.toString(), MediaType.VIDEO)
        )
    }

    val pickAudioFile = pickAudioFile(permissionDenied = viewModel::onPermissionDenied) {
        viewModel.openMediaFile(
            MediaFileArgument(it.toString(), MediaType.AUDIO)
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
