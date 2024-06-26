package com.javernaut.whatthecodec.home.ui.stream

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.compose.theme.WhatTheCodecTheme

@Composable
fun StreamFeatureItem(
    title: String,
    value: String,
    onCopyValue: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        var expanded by remember { mutableStateOf(false) }
        StreamFeatureContent(
            title = title,
            value = value
        ) {
            expanded = true
        }
        CopyToClipboardDropdown(
            expanded = expanded,
            title = value,
            dismissCallback = { expanded = false }
        ) {
            onCopyValue(value)
        }
    }
}

@PreviewLightDark
@Composable
fun PreviewStreamFeature() {
    WhatTheCodecTheme.Static {
        Surface {
            StreamFeatureContent(
                "Some title",
                "Some value"
            ) { }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun StreamFeatureContent(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    longClickListener: () -> Unit
) {
    Column(
        modifier
            .combinedClickable(onLongClick = longClickListener, onClick = {})
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
        verticalArrangement = spacedBy(8.dp)
    ) {
        Text(
            title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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
