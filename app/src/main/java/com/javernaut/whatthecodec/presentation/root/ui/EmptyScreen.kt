package com.javernaut.whatthecodec.presentation.root.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.compose.theme3.WhatTheCodecM3Theme

@Composable
fun EmptyScreen(
    onVideoIconClick: () -> Unit,
    onAudioIconClick: () -> Unit,
    onSettingsClicked: () -> Unit
) {
    Scaffold(
        topBar = {
            EmptyScreenTopAppBar(onSettingsClicked)
        }
    ) {
        EmptyScreenContent(onVideoIconClick, onAudioIconClick, Modifier.padding(it))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmptyScreenTopAppBar(
    onSettingsClicked: () -> Unit = {}
) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.app_name)) },
        actions = {
            IconButton(onClick = onSettingsClicked) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = stringResource(id = R.string.menu_settings),
                )
            }
        }
    )
}

@Composable
private fun EmptyScreenContent(
    onVideoIconClick: () -> Unit,
    onAudioIconClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(id = R.string.empty_root_description),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.size(24.dp))
            Text(
                text = stringResource(id = R.string.empty_root_choose),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.size(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                EmptyScreenIcon(
                    Icons.Filled.Videocam,
                    R.string.tab_video,
                    onVideoIconClick
                )
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = stringResource(id = R.string.empty_root_or),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                EmptyScreenIcon(
                    Icons.Filled.MusicNote,
                    R.string.tab_audio,
                    onAudioIconClick
                )
            }
        }
    }
}

@Composable
private fun EmptyScreenIcon(
    image: ImageVector,
    contentDescriptionResId: Int,
    clickListener: () -> Unit
) {
    val textContent = stringResource(id = contentDescriptionResId)

    ExtendedFloatingActionButton(
        onClick = clickListener,
        icon = { Icon(image, textContent) },
        text = { Text(text = textContent) },
    )
}

@PreviewLightDark
@Composable
private fun EmptyScreenPreview() {
    WhatTheCodecM3Theme {
        EmptyScreen({}, {}, {})
    }
}
