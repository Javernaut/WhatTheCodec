package com.javernaut.whatthecodec.presentation.compose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Color.White,
    primaryVariant = Color.White,
    onPrimary = Color.Black,
    secondary = Color.White,
    secondaryVariant = Color.White,
    onSecondary = Color.Black
)

private val LightColorPalette = lightColors(
    primary = BrandBlue,
    primaryVariant = BrandBlueVariant,
    secondary = BrandBlue,
    onPrimary = Color.White,
    surface = ThemeLightSurface,
    onSurface = Color.Black,
    background = ThemeLightBackground
)

@Composable
fun WhatTheCodecTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        shapes = Shapes,
        content = content
    )
}