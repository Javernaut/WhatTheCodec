package com.javernaut.whatthecodec.presentation.settings

import android.preference.PreferenceManager
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Checkbox
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.javernaut.whatthecodec.R

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
fun MultiSelectListPreference(
    key: String,
    defaultValue: Set<String>,
    title: String,
    displayableEntries: List<String>,
    entriesCodes: List<String>,
    summaryBuilder: (List<String>) -> String = { it.joinToString() },
    onNewCodeSelected: ((Set<String>) -> Unit)? = null
) {
    val applicationContext = LocalContext.current.applicationContext
    val defaultSharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(applicationContext)
    val selectedItemCodes = defaultSharedPreferences.getStringSet(key, defaultValue)!!
    val currentlySelectedItemIndexes = List(displayableEntries.size) {
        selectedItemCodes.contains(entriesCodes[it])
    }

    var dialogOpened by remember { mutableStateOf(false) }
    Preference(
        title = title,
        // TODO what if nothing is selected ?
        summary = summaryBuilder(displayableEntries.filterIndexed { index, s ->
            currentlySelectedItemIndexes[index]
        })
    ) {
        dialogOpened = true
    }

    if (dialogOpened) {
        MultiChoicePreferenceDialog(
            title = title,
            onDismissRequest = { dialogOpened = false },
            items = displayableEntries,
            initialSelectedPositions = currentlySelectedItemIndexes
        ) { resultList ->
            val newValueToSet = entriesCodes.filterIndexed { index, s ->
                resultList[index]
            }.toSet()
            defaultSharedPreferences
                .edit()
                .putStringSet(key, newValueToSet)
                .apply()
            onNewCodeSelected?.invoke(newValueToSet)
        }
    }
}

@Composable
fun MultiChoicePreferenceDialog(
    title: String,
    onDismissRequest: () -> Unit,
    items: List<String>,
    initialSelectedPositions: List<Boolean>,
    resultListener: (List<Boolean>) -> Unit
) {
    val itemsStates = remember {
        initialSelectedPositions.map { mutableStateOf(it) }
    }
    PreferenceDialog(title, onDismissRequest,
        buttons = {
            CancelDialogButton(onDismissRequest)
            Spacer(modifier = Modifier.width(8.dp))
            TextButton(onClick = {
                resultListener(itemsStates.map { it.value })
                onDismissRequest()
            }) {
                Text(
                    stringResource(id = android.R.string.ok).toUpperCase(),
                )
            }
        },
        content = {
            // TODO Consider scrolling
            items.forEachIndexed { index, item ->
                PreferenceCheckboxButton(
                    item, itemsStates[index]
                )
            }
        })
}

@Composable
private fun CancelDialogButton(onDismissRequest: () -> Unit) {
    TextButton(onClick = onDismissRequest) {
        Text(
            stringResource(id = android.R.string.cancel).toUpperCase(),
        )
    }
}

@Composable
private fun PreferenceDialog(
    title: String,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    PreferenceDialog(
        title, onDismissRequest,
        buttons = {
            CancelDialogButton(onDismissRequest)
        }, content = content
    )
}

@Composable
private fun PreferenceDialog(
    title: String,
    onDismissRequest: () -> Unit,
    buttons: @Composable RowScope.() -> Unit = {
        CancelDialogButton(onDismissRequest)
    },
    content: @Composable () -> Unit
) {
    WtcDialog(
        title, onDismissRequest,
        buttons = buttons,
        content = content
    )
}

@Composable
private fun PreferenceRadioButton(
    text: String,
    selected: Boolean,
    clickListener: () -> Unit
) {
    PreferenceItemRow(clickListener) {
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
private fun PreferenceCheckboxButton(
    text: String,
    selected: MutableState<Boolean>,
) {
    PreferenceItemRow(clickListener = {
        selected.value = !selected.value
    }) {
        Checkbox(
            checked = selected.value,
            onCheckedChange = {
                selected.value = it
            }
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
private fun PreferenceItemRow(
    clickListener: () -> Unit,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = dimensionResource(id = R.dimen.common_clickable_item_height))
            .clickable(onClick = clickListener)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
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
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                summary,
                style = MaterialTheme.typography.body2,
            )
        }
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
