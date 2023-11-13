package com.javernaut.whatthecodec.presentation.stream

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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.javernaut.whatthecodec.presentation.compose.theme.m3.WhatTheCodecTheme
import io.github.javernaut.mediafile.MediaStream

@Composable
fun StreamFeatureItem(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        var expanded by remember { mutableStateOf(false) }
        StreamFeature(title, value) {
            expanded = true
        }
        val context = LocalContext.current
        CopyToClipboardDropdown(
            expanded,
            value,
            { expanded = false }
        ) {
            copyTextToClipboard(context, value)
        }
    }
}

@Composable
fun <T : MediaStream> StreamFeatureItem(
    stream: T,
    streamFeature: StreamFeature<T>,
    modifier: Modifier = Modifier
) {
    streamFeature.getValue(stream, LocalContext.current.resources)?.let { value ->
        StreamFeatureItem(
            stringResource(id = streamFeature.title).toUpperCase(),
            value,
            modifier
        )
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
    WhatTheCodecTheme(useDarkTheme = false) {
        Surface {
            StreamFeature(
                stringResource(id = R.string.page_audio_codec_name).toUpperCase(),
                "Some value"
            ) { }
        }
    }
}

@Preview
@Composable
fun PreviewStreamFeatureInDark() {
    WhatTheCodecTheme(useDarkTheme = true) {
        Surface {
            StreamFeature(
                stringResource(id = R.string.page_audio_codec_name).toUpperCase(),
                "Some value"
            ) { }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun StreamFeature(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
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
        Text(
            title,
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(Modifier.height(8.dp))
        Text(
            value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun CopyToClipboardDropdown(
    expanded: Boolean,
    title: String,
    dismissCallback: () -> Unit,
    copyCallback: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = dismissCallback
    ) {
        DropdownMenuItem(enabled = false, onClick = { }, text = {
            Text(title, color = MaterialTheme.colorScheme.primary)
        })
        DropdownMenuItem(onClick = {
            copyCallback()
            dismissCallback()
        }, text = {
            Text(stringResource(id = R.string.stream_copy))
        })
    }
}
