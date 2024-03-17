package com.javernaut.whatthecodec.presentation.compose.playground

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.javernaut.whatthecodec.presentation.compose.preference.MultiChoicePreferenceDialog
import com.javernaut.whatthecodec.presentation.compose.theme.WhatTheCodecTheme

class ComposePlaygroundActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var savedSelection = listOf(
            true, false, true,
            true, false, true,
            true, false, true,
            true, false, true,
            true, false, true,
            true, false, true,
        )
        setContent {
            WhatTheCodecTheme {
                var dialogOpened by remember { mutableStateOf(true) }
                Button(onClick = { dialogOpened = true }) {
                    Text(text = "Open dialog")
                }
                if (dialogOpened) {
                    val onDismissRequest = { dialogOpened = false }
                    val items = listOf(
                        "Light1", "Dark1", "Three1",
                        "Light2", "Dark2", "Three2",
                        "Light3", "Dark3", "Three3",
                        "Light4", "Dark4", "Three4",
                        "Light5", "Dark5", "Three5",
                        "Light6", "Dark6", "Three6",
                    )
                    MultiChoicePreferenceDialog(
                        "App Theme",
                        onDismissRequest,
                        items,
                        savedSelection
                    ) { resultSelectedItems ->
                        savedSelection = resultSelectedItems
                        val resultText = items.filterIndexed { index: Int, s: String ->
                            resultSelectedItems[index]
                        }.joinToString()
                        Toast.makeText(this, resultText, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}
