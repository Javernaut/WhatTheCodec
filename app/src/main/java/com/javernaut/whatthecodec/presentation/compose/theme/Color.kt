package com.javernaut.whatthecodec.presentation.compose.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

private val primaryLight = Color(0xFF004BAB)
private val onPrimaryLight = Color(0xFFFFFFFF)
private val primaryContainerLight = Color(0xFF276FE5)
private val onPrimaryContainerLight = Color(0xFFFFFFFF)
private val secondaryLight = Color(0xFF00639B)
private val onSecondaryLight = Color(0xFFFFFFFF)
private val secondaryContainerLight = Color(0xFF30AAFF)
private val onSecondaryContainerLight = Color(0xFF00192D)
private val tertiaryLight = Color(0xFF71358C)
private val onTertiaryLight = Color(0xFFFFFFFF)
private val tertiaryContainerLight = Color(0xFF995BB3)
private val onTertiaryContainerLight = Color(0xFFFFFFFF)
private val errorLight = Color(0xFFBA1A1A)
private val onErrorLight = Color(0xFFFFFFFF)
private val errorContainerLight = Color(0xFFFFDAD6)
private val onErrorContainerLight = Color(0xFF410002)
private val backgroundLight = Color(0xFFFAF8FF)
private val onBackgroundLight = Color(0xFF191B23)
private val surfaceLight = Color(0xFFFAF8FF)
private val onSurfaceLight = Color(0xFF191B23)
private val surfaceVariantLight = Color(0xFFDEE2F2)
private val onSurfaceVariantLight = Color(0xFF424754)
private val outlineLight = Color(0xFF727785)
private val outlineVariantLight = Color(0xFFC2C6D6)
private val scrimLight = Color(0xFF000000)
private val inverseSurfaceLight = Color(0xFF2E3038)
private val inverseOnSurfaceLight = Color(0xFFEFF0FA)
private val inversePrimaryLight = Color(0xFFAFC6FF)
private val surfaceDimLight = Color(0xFFD8D9E4)
private val surfaceBrightLight = Color(0xFFFAF8FF)
private val surfaceContainerLowestLight = Color(0xFFFFFFFF)
private val surfaceContainerLowLight = Color(0xFFF2F3FD)
private val surfaceContainerLight = Color(0xFFECEDF8)
private val surfaceContainerHighLight = Color(0xFFE7E7F2)
private val surfaceContainerHighestLight = Color(0xFFE1E2EC)

private val primaryDark = Color(0xFFAFC6FF)
private val onPrimaryDark = Color(0xFF002D6D)
private val primaryContainerDark = Color(0xFF0056C2)
private val onPrimaryContainerDark = Color(0xFFFFFFFF)
private val secondaryDark = Color(0xFF97CBFF)
private val onSecondaryDark = Color(0xFF003353)
private val secondaryContainerDark = Color(0xFF007ABE)
private val onSecondaryContainerDark = Color(0xFFFFFFFF)
private val tertiaryDark = Color(0xFFEBB2FF)
private val onTertiaryDark = Color(0xFF4E0F69)
private val tertiaryContainerDark = Color(0xFF995BB3)
private val onTertiaryContainerDark = Color(0xFFFFFFFF)
private val errorDark = Color(0xFFFFB4AB)
private val onErrorDark = Color(0xFF690005)
private val errorContainerDark = Color(0xFF93000A)
private val onErrorContainerDark = Color(0xFFFFDAD6)
private val backgroundDark = Color(0xFF11131A)
private val onBackgroundDark = Color(0xFFE1E2EC)
private val surfaceDark = Color(0xFF11131A)
private val onSurfaceDark = Color(0xFFE1E2EC)
private val surfaceVariantDark = Color(0xFF424754)
private val onSurfaceVariantDark = Color(0xFFC2C6D6)
private val outlineDark = Color(0xFF8C909F)
private val outlineVariantDark = Color(0xFF424754)
private val scrimDark = Color(0xFF000000)
private val inverseSurfaceDark = Color(0xFFE1E2EC)
private val inverseOnSurfaceDark = Color(0xFF2E3038)
private val inversePrimaryDark = Color(0xFF0059C8)
private val surfaceDimDark = Color(0xFF11131A)
private val surfaceBrightDark = Color(0xFF373941)
private val surfaceContainerLowestDark = Color(0xFF0B0E15)
private val surfaceContainerLowDark = Color(0xFF191B23)
private val surfaceContainerDark = Color(0xFF1D1F27)
private val surfaceContainerHighDark = Color(0xFF272A31)
private val surfaceContainerHighestDark = Color(0xFF32353C)

val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)
