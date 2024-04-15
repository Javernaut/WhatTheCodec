package com.javernaut.whatthecodec.compose.preference

import android.preference.PreferenceManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.compose.common.WtcDialog

@Composable
fun ListPreference(
    title: String,
    displayableEntries: List<String>,
    selectedItemIndex: Int,
    onNewCodeSelected: (Int) -> Unit
) {
    var dialogOpened by rememberSaveable { mutableStateOf(false) }
    Preference(
        title = title,
        summary = displayableEntries[selectedItemIndex]
    ) {
        dialogOpened = true
    }

    if (dialogOpened) {
        SingleChoicePreferenceDialog(
            title = title,
            dismissRequest = { dialogOpened = false },
            items = displayableEntries,
            currentlySelectedIndex = selectedItemIndex
        ) {
            onNewCodeSelected(it)
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
    var selectedIndex by rememberSaveable { mutableIntStateOf(currentlySelectedIndex) }
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

    var dialogOpened by rememberSaveable { mutableStateOf(false) }
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
                var value by itemsStates[index]
                PreferenceCheckboxButton(
                    items[index], value
                ) {
                    value = it
                }
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
    PreferenceItemRow(
        clickListener, selected
    ) {
        RadioButton(
            selected = selected,
            onClick = null
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun PreferenceCheckboxButton(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    PreferenceItemRow(
        clickListener = {
            onCheckedChange(!checked)
        },
        checked = checked
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = null
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun PreferenceItemRow(
    clickListener: () -> Unit,
    checked: Boolean,
    content: @Composable RowScope.() -> Unit
) {
    Surface(
        onClick = clickListener,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = if (checked) 8.dp else 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .sizeIn(minHeight = 48.dp)
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = spacedBy(8.dp),
            content = content
        )
    }
}

@Composable
private fun ListPreferenceDialogContent(content: LazyListScope.() -> Unit) {
    Box {
        val listState = rememberLazyListState()
        LazyColumn(
            state = listState,
            verticalArrangement = spacedBy(4.dp),
            content = content
        )

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
