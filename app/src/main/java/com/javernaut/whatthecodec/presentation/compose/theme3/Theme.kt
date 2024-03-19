package com.javernaut.whatthecodec.presentation.compose.theme3

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable


@Composable
fun WhatTheCodecM3Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) darkScheme else lightScheme
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
