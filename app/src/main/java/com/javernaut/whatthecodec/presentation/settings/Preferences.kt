package com.javernaut.whatthecodec.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.javernaut.whatthecodec.R

@Composable
fun SingleChoicePreferenceDialog(
    title: String,
    onDismissRequest: () -> Unit,
    items: List<String>,
    currentlySelectedModeIndex: Int,
    clickListener: (Int) -> Unit
) {
    Dialog(title, onDismissRequest,
        buttons = {
            TextButton(onClick = onDismissRequest) {
                Text(
                    stringResource(id = android.R.string.cancel).toUpperCase(),
                    color = MaterialTheme.colors.onPrimary
                )
            }
        }) {
        items.forEachIndexed { index, item ->
            PreferenceRadioButton(
                item, index == currentlySelectedModeIndex
            ) {
                clickListener(index)
                onDismissRequest()
            }
        }
    }
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