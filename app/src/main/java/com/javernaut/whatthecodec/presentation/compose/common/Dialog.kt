package com.javernaut.whatthecodec.presentation.compose.common

import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable

@Composable
fun WtcDialog(
    title: String,
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    AlertDialog(
        title = { androidx.compose.material3.Text(text = title) },
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        text = content
    )
}
