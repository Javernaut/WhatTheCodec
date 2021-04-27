package com.javernaut.whatthecodec.presentation.root.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.javernaut.whatthecodec.R

@Composable
fun EmptyScreen(
    onVideoIconClick: () -> Unit,
    onAudioIconClick: () -> Unit,
    menuActions: @Composable RowScope.() -> Unit = {},
) {
    Scaffold(
        topBar = { EmptyScreenTopAppBar(menuActions) }
    ) {
        EmptyScreenContent(Modifier.padding(it), onVideoIconClick, onAudioIconClick)
    }
}

@Composable
private fun EmptyScreenTopAppBar(
    menuActions: @Composable RowScope.() -> Unit
) {
    TopAppBar(title = {
        Text(text = stringResource(id = R.string.app_name))
    }, actions = menuActions)
}

@Composable
private fun EmptyScreenContent(
    modifier: Modifier = Modifier,
    onVideoIconClick: () -> Unit,
    onAudioIconClick: () -> Unit
) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(id = R.string.empty_root_description),
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.onSurface
            )
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text = stringResource(id = R.string.empty_root_choose),
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.onSurface
            )
            Spacer(modifier = Modifier.size(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                EmptyScreenIcon(
                    R.drawable.ic_menu_video,
                    R.string.menu_pick_video,
                    onVideoIconClick
                )
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = stringResource(id = R.string.empty_root_or),
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface
                )
                EmptyScreenIcon(
                    R.drawable.ic_menu_audio,
                    R.string.menu_pick_audio,
                    onAudioIconClick
                )
            }
        }
    }
}

@Composable
private fun EmptyScreenIcon(
    iconResId: Int,
    contentDescriptionResId: Int,
    clickListener: () -> Unit
) {
    Icon(
        painter = painterResource(id = iconResId),
        contentDescription = stringResource(id = contentDescriptionResId),
        tint = MaterialTheme.colors.secondary,
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = false),
                onClick = clickListener
            )
    )
}

