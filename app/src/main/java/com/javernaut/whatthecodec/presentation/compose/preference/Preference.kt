package com.javernaut.whatthecodec.presentation.compose.preference

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.javernaut.whatthecodec.presentation.compose.theme.WhatTheCodecTheme

@Composable
fun Preference(
    title: String,
    summary: String,
    clickHandler: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = clickHandler)
            .padding(
                start = commonStartPadding, top = 16.dp, end = 8.dp, bottom = 16.dp
            )
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium
        )
        // TODO ContentAlpha.medium
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
fun PreferenceTitle(@StringRes title: Int) {
    Text(
        stringResource(id = title),
        Modifier
            .fillMaxWidth()
            .padding(start = commonStartPadding, top = 24.dp, end = 8.dp, bottom = 8.dp),
        style = MaterialTheme.typography.titleSmall,
// TODO        color = MaterialTheme.colors.secondary
        color = MaterialTheme.colorScheme.secondary
    )
}

// TODO Check if this should be different and adjust the TopAppBar if so
// https://m3.material.io/components/top-app-bar/specs#2be41d8e-79cf-4f1a-abc1-962744ca9291
private val commonStartPadding = 56.dp

@Preview
@Composable
fun PreferencePreview() {
    WhatTheCodecTheme {
        Preference(
            title = "Title",
            summary = "Summary",
            clickHandler = {}
        )
    }
}
