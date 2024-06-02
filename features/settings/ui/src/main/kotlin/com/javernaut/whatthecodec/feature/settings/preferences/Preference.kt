package com.javernaut.whatthecodec.feature.settings.preferences

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun Preference(
    title: String,
    summary: String,
    clickHandler: () -> Unit
) {
    Column(
        Modifier
            .clickable(onClick = clickHandler)
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .padding(16.dp)
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            summary,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
fun Preference(
    @StringRes title: Int,
    @StringRes summary: Int,
    clickHandler: () -> Unit
) =
    Preference(
        title = stringResource(id = title),
        summary = stringResource(id = summary),
        clickHandler = clickHandler
    )

@Composable
fun PreferenceGroup(
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(content = content)
    }
}

@Composable
fun PreferenceGroup(
    @StringRes title: Int,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        PreferenceTitle(title = title)
        PreferenceGroup(content = content)
    }
}

@Composable
fun PreferenceDivider() {
    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
}

@Composable
private fun PreferenceTitle(@StringRes title: Int) {
    Text(
        stringResource(id = title),
        Modifier
            .fillMaxWidth()
            .heightIn(min = 32.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.secondary
    )
}

// TODO Pull the theming to a core module
//@Preview
//@Composable
//private fun PreferencePreview() {
//    WhatTheCodecTheme.Static {
//        PreferenceGroup {
//            Preference(
//                title = "Title",
//                summary = "Summary",
//                clickHandler = {}
//            )
//        }
//    }
//}

//@PreviewLightDark
//@Composable
//private fun PreviewPreferenceGroup() {
//    WhatTheCodecTheme.Static {
//        Column {
//            PreferenceGroup(R.string.settings_title) {
//                Preference(title = "Title", summary = "Summary") {
//
//                }
//                PreferenceDivider()
//                Preference(title = "Title3", summary = "Summary2") {
//
//                }
//                PreferenceDivider()
//                Preference(title = "Title3", summary = "Summary3") {
//
//                }
//            }
//        }
//    }
//}
