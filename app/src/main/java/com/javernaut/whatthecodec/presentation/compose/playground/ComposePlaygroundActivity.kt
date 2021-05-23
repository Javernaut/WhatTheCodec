package com.javernaut.whatthecodec.presentation.compose.playground

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.javernaut.whatthecodec.presentation.compose.theme.WhatTheCodecTheme
import com.javernaut.whatthecodec.presentation.settings.SingleChoicePreferenceDialog

class ComposePlaygroundActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WhatTheCodecTheme {
                var dialogOpened by remember { mutableStateOf(true) }
                Button(onClick = { dialogOpened = true }) {
                    Text(text = "Open dialog")
                }
                if (dialogOpened) {
                    val onDismissRequest = { dialogOpened = false }
                    SingleChoicePreferenceDialog(
                        "App Theme",
                        onDismissRequest,
                        listOf("Light", "Dark", "Three"),
                        1
                    ) {
                        onDismissRequest()
                    }
                }
            }
        }
    }
}