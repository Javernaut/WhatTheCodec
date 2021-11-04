package com.javernaut.whatthecodec.presentation.compose.preference

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
            .fillMaxWidth()
            .clickable(onClick = clickHandler)
            .padding(
                start = 72.dp, top = 16.dp, end = 8.dp, bottom = 16.dp
            )
    ) {
        Text(
            title,
            style = MaterialTheme.typography.subtitle1
        )
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                summary,
                style = MaterialTheme.typography.body2,
            )
        }
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
fun PreferenceDivider() {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(if (isSystemInDarkTheme()) 0xFF282828 else 0xFFD9D9D9))
    )
}


@Composable
fun PreferenceTitle(@StringRes title: Int) {
    Text(
        stringResource(id = title),
        Modifier
            .fillMaxWidth()
            .padding(start = 72.dp, top = 24.dp, end = 8.dp, bottom = 8.dp),
        style = MaterialTheme.typography.subtitle2,
        color = MaterialTheme.colors.secondary
    )
}