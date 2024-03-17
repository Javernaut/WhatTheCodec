package com.javernaut.whatthecodec.presentation.compose.preference

import android.preference.PreferenceManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.compose.common.WtcDialog

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
            dismissRequest = { dialogOpened = false },
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
    dismissRequest: () -> Unit,
    items: List<String>,
    currentlySelectedIndex: Int,
    clickListener: (Int) -> Unit
) {
    var selectedIndex by remember { mutableStateOf(currentlySelectedIndex) }
    PreferenceDialog(title, dismissRequest,
        applyRequest = {
            clickListener(selectedIndex)
        }
    ) {
        ListPreferenceDialogContent {
            items(items.size,
                key = {
                    items[it]
                }
            ) { index ->
                PreferenceRadioButton(
                    items[index], index == selectedIndex
                ) {
                    selectedIndex = index
                }
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
        summary = summaryBuilder(displayableEntries.filterIndexed { index, s ->
            currentlySelectedItemIndexes[index]
        }).ifEmpty {
            stringResource(id = R.string.settings_nothing_is_selected)
        }
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
        applyRequest = {
            resultListener(itemsStates.map { it.value })
        }
    ) {
        ListPreferenceDialogContent {
            items(
                items.size,
                key = {
                    items[it]
                }
            ) { index ->
                PreferenceCheckboxButton(
                    items[index], itemsStates[index]
                )
            }
        }
    }
}

@Composable
private fun ApplyDialogButton(applyRequest: () -> Unit, dismissRequest: () -> Unit) {
    TextButton(onClick = {
        applyRequest()
        dismissRequest()
    }) {
        Text(
            stringResource(id = android.R.string.ok),
        )
    }
}

@Composable
private fun CancelDialogButton(dismissRequest: () -> Unit) {
    TextButton(onClick = dismissRequest) {
        Text(
            stringResource(id = android.R.string.cancel),
        )
    }
}

@Composable
private fun PreferenceDialog(
    title: String,
    dismissRequest: () -> Unit,
    applyRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    WtcDialog(
        title,
        dismissRequest,
        dismissButton = { CancelDialogButton(dismissRequest) },
        confirmButton = { ApplyDialogButton(applyRequest, dismissRequest) },
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
        // TODO Check if the comment still takes place for M3
        // The onClick is null, which means there will be no enforcing of 48dp touch area
        RadioButton(
            modifier = Modifier.padding(horizontal = 24.dp),
            selected = selected,
            onClick = null
        )
        Text(
            modifier = Modifier.padding(end = 24.dp),
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 18.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
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
        // The onClick is not null, so the size of the Checkbox itself will be 48dp.
        // So applying smaller paddings.
        Checkbox(
            modifier = Modifier.padding(horizontal = 12.dp),
            checked = selected.value,
            onCheckedChange = {
                selected.value = it
            }
        )
        Text(
            modifier = Modifier.padding(end = 24.dp),
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 18.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
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
            .clickable(onClick = clickListener),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Composable
private fun ListPreferenceDialogContent(content: LazyListScope.() -> Unit) {
    Box {
        val listState = rememberLazyListState()
        LazyColumn(state = listState, content = content)

        // Top divider
        ListPreferenceDialogDivider(
            // TODO Check if a derived state can be used here
            visible = listState.canScrollBackward,
            modifier = Modifier.align(Alignment.TopCenter),
        )

        // Bottom divider
        ListPreferenceDialogDivider(
            // TODO Check if a derived state can be used here
            visible = listState.canScrollForward,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun ListPreferenceDialogDivider(
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        HorizontalDivider()
    }
}
