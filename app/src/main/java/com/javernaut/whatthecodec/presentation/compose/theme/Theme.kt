package com.javernaut.whatthecodec.presentation.compose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = BrandDark,
    primaryVariant = BrandDarkVariant,
    secondary = Color.White,
    onPrimary = Color.White,
    surface = BrandDark,
    onSurface = Color.White,
    background = ThemeDarkBackground
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

val Colors.secondaryText
    get() = if (isLight) {
        ColorTextSecondaryLight
    } else {
        ColorTextSecondaryDark
    }

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
//        typography = Typography,
        shapes = Shapes,
        typography = MaterialTheme.typography.copy(
            caption =
            MaterialTheme.typography.caption.copy(
                color = colors.secondaryText
            )
        ),
        content = content
    )
}