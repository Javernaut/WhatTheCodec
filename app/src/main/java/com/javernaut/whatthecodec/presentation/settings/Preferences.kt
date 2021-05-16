package com.javernaut.whatthecodec.presentation.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.preference.PreferenceManager
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.compose.theme.secondaryText

@Composable
fun ListPreference(
    key: String,
    defaultValue: String,
    title: String,
    displayableEntries: List<String>,
    entriesCodes: List<String>,
    onNewCodeSelected: (String) -> Unit
) {
    val applicationContext = LocalContext.current.applicationContext
    val defaultSharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(applicationContext)
    val selectedItemCode = defaultSharedPreferences.getString(key, defaultValue)

    val currentlySelectedItemIndex = entriesCodes.indexOf(selectedItemCode)

    var dialogOpened by remember { mutableStateOf(false) }
    Preference(
        title = title,
        summary = displayableEntries[currentlySelectedItemIndex]
    ) {
        dialogOpened = true
    }

    if (dialogOpened) {
        SingleChoicePreferenceDialog(
            title = title,
            onDismissRequest = { dialogOpened = false },
            items = displayableEntries,
            currentlySelectedIndex = currentlySelectedItemIndex
        ) {
            val newValueToSet = entriesCodes[it]
            defaultSharedPreferences
                .edit()
                .putString(key, newValueToSet)
                .apply()
            onNewCodeSelected(newValueToSet)
        }
    }
}


@Composable
fun SingleChoicePreferenceDialog(
    title: String,
    onDismissRequest: () -> Unit,
    items: List<String>,
    currentlySelectedIndex: Int,
    clickListener: (Int) -> Unit
) {
    PreferenceDialog(title, onDismissRequest) {
        items.forEachIndexed { index, item ->
            PreferenceRadioButton(
                item, index == currentlySelectedIndex
            ) {
                clickListener(index)
                onDismissRequest()
            }
        }
    }
}

@Composable
private fun PreferenceDialog(
    title: String,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    Dialog(
        title, onDismissRequest,
        buttons = {
            TextButton(onClick = onDismissRequest) {
                Text(
                    stringResource(id = android.R.string.cancel).toUpperCase(),
                    color = MaterialTheme.colors.onPrimary
                )
            }
        }, content = content
    )
}

@Composable
private fun PreferenceRadioButton(
    text: String,
    selected: Boolean,
    clickListener: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = dimensionResource(id = R.dimen.common_clickable_item_height))
            .clickable { clickListener() }
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null
        )
        Spacer(modifier = Modifier.width(24.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.body1.copy(
                fontSize = 18.sp
            ),
            color = MaterialTheme.colors.onSurface
        )
    }
}

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
        Text(
            summary,
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.secondaryText
        )
    }
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
