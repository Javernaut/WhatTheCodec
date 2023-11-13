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
import androidx.compose.ui.text.font.FontWeight
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
                start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp
            )
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            summary,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurfaceVariant
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
            .padding(start = 16.dp, top = 24.dp, end = 16.dp, bottom = 8.dp),
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary
    )
}
