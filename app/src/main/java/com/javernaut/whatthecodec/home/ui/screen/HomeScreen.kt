package com.javernaut.whatthecodec.home.ui.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.javernaut.whatthecodec.R
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
        EmptyHomeScreen(
            onVideoIconClick,
            onAudioIconClick,
            onSettingsClicked
        )
    } else {
        MainHomeScreen(
            screenState!!,
            onVideoIconClick,
            onAudioIconClick,
            onSettingsClicked
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
