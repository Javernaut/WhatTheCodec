package com.javernaut.whatthecodec.presentation.stream.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.stream.model.StreamFeature

@OptIn(ExperimentalFoundationApi::class)
class StreamFeatureViewHolder(private val itemComposeView: ComposeView) :
    RecyclerView.ViewHolder(itemComposeView) {

    private lateinit var streamFeature: StreamFeature

    fun bindTo(streamFeature: StreamFeature) {
        this.streamFeature = streamFeature

        itemComposeView.setContent {
            WhatTheCodecTheme {
                var expanded by remember { mutableStateOf(false) }
                StreamFeature(streamFeature = streamFeature) {
                    expanded = true
                }
                CopyToClipboardDropdown(expanded, streamFeature.description) {
                    expanded = false
                }
            }
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

@ExperimentalFoundationApi
@Preview
@Composable
fun PreviewStreamFeature() {
    WhatTheCodecTheme {
        Surface {
            val streamFeature = StreamFeature(
                R.string.page_audio_codec_name,
                "Some value"
            )
            var expanded by remember { mutableStateOf(false) }
            StreamFeature(streamFeature = streamFeature) {
                expanded = true
            }
            CopyToClipboardDropdown(expanded, streamFeature.description) {
                expanded = false
            }
        }
    }
}

@ExperimentalFoundationApi
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
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            stringResource(id = streamFeature.title).toUpperCase(),
            style = MaterialTheme.typography.caption
        )
        Spacer(Modifier.height(8.dp))
        Text(
            streamFeature.description,
            style = MaterialTheme.typography.body1
        )
    }
}

@Composable
fun CopyToClipboardDropdown(expanded: Boolean, valueToCopy: String, dismissCallback: () -> Unit) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = dismissCallback
    ) {
        DropdownMenuItem(enabled = false, onClick = { }) {
            Text(valueToCopy, color = MaterialTheme.colors.primary)
        }
        DropdownMenuItem(onClick = {
//                val context = LocalContext.current
//                copyTextToClipboard(context, "ValueToCopy")
            dismissCallback()
        }) {
            Text(stringResource(id = R.string.stream_copy))
        }
    }
}


@Composable
fun WhatTheCodecTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        typography = MaterialTheme.typography.copy(
            caption =
            MaterialTheme.typography.caption.copy(
                color = colorResource(id = R.color.secondary_text_default_material_light)
            )
        ),
        colors = MaterialTheme.colors.copy(
            primary = colorResource(id = R.color.brand_blue)
        ),
        content = content
    )
}
