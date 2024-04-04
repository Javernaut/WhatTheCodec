package com.javernaut.whatthecodec.home.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.javernaut.whatthecodec.home.presentation.MediaFileViewModel

@Composable
fun HomeScreen(
    viewModel: MediaFileViewModel,
    onVideoIconClick: () -> Unit,
    onAudioIconClick: () -> Unit,
    onSettingsClicked: () -> Unit
) {
    val screenState by viewModel.screenState.observeAsState()
    if (screenState == null) {
        EmptyScreen(
            onVideoIconClick,
            onAudioIconClick,
            onSettingsClicked
        )
    } else {
        MainScreen(
            screenState!!,
            onVideoIconClick,
            onAudioIconClick,
            onSettingsClicked
        )
    }
}
