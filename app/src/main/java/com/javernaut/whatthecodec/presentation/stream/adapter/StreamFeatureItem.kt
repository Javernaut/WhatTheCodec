package com.javernaut.whatthecodec.presentation.stream.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.getSystemService
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.compose.theme.WhatTheCodecTheme
import com.javernaut.whatthecodec.presentation.stream.model.StreamFeature

@Composable
fun StreamFeatureItem(
    modifier: Modifier = Modifier,
    streamFeature: StreamFeature
) {
    Box(modifier = modifier) {
        var expanded by remember { mutableStateOf(false) }
        StreamFeature(streamFeature = streamFeature) {
            expanded = true
        }
        val context = LocalContext.current
        CopyToClipboardDropdown(
            expanded,
            streamFeature.description,
            { expanded = false }
        ) {
            copyTextToClipboard(context, streamFeature.description)
        }
    }
}

private fun copyTextToClipboard(context: Context, valueToCopy: String) {
    val clipboardManager = context.getSystemService<ClipboardManager>()!!
    clipboardManager.setPrimaryClip(
        ClipData.newPlainText(valueToCopy, valueToCopy)
    )
    val toastMessage = context.getString(
        R.string.stream_text_copied_pattern, valueToCopy
    )
    Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
}

@Preview
@Composable
fun PreviewStreamFeatureInLight() {
    WhatTheCodecTheme(darkTheme = false) {
        Surface {
            val streamFeature = StreamFeature(
                R.string.page_audio_codec_name,
                "Some value"
            )
            StreamFeature(streamFeature = streamFeature) { }
        }
    }
}

@Preview
@Composable
fun PreviewStreamFeatureInDark() {
    WhatTheCodecTheme(darkTheme = true) {
        Surface {
            val streamFeature = StreamFeature(
                R.string.page_audio_codec_name,
                "Some value"
            )
            StreamFeature(streamFeature = streamFeature) { }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StreamFeature(
    modifier: Modifier = Modifier,
    streamFeature: StreamFeature,
    longClickListener: () -> Unit
) {
    Column(
        modifier
            .combinedClickable(onLongClick = longClickListener, onClick = {})
            .fillMaxWidth()
            .defaultMinSize(minHeight = 58.dp)
            .padding(
                horizontal = dimensionResource(id = R.dimen.common_horizontal_spacing),
                vertical = 8.dp
            )
    ) {
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                stringResource(id = streamFeature.title).toUpperCase(),
                style = MaterialTheme.typography.caption
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            streamFeature.description,
            style = MaterialTheme.typography.body1
        )
    }
}

@Composable
fun CopyToClipboardDropdown(
    expanded: Boolean,
    title: String,
    dismissCallback: () -> Unit,
    copyCallback: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = dismissCallback
    ) {
        DropdownMenuItem(enabled = false, onClick = { }) {
            val colors = MaterialTheme.colors
            Text(title, color = if (colors.isLight) colors.primary else colors.secondary)
        }
        DropdownMenuItem(onClick = {
            copyCallback()
            dismissCallback()
        }) {
            Text(stringResource(id = R.string.stream_copy))
        }
    }
}
