package com.javernaut.whatthecodec.presentation.compose.common

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.AppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun getAppBarElevation() = if (isSystemInDarkTheme()) 1.dp else AppBarDefaults.TopAppBarElevation