package com.javernaut.whatthecodec.presentation.compose.preference

import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun SharedPreferences.getStringSetAsState(
    key: String,
    defaultValue: Set<String>?
) = getValueAsState(
    key = key,
    defaultValue = defaultValue,
    valueExtractor = SharedPreferences::getStringSet
)

@Composable
private fun <T> SharedPreferences.getValueAsState(
    key: String,
    defaultValue: T?,
    valueExtractor: SharedPreferences.(String, T?) -> T?
): State<T?> {
    val resultState = remember {
        mutableStateOf(valueExtractor(key, defaultValue))
    }

    val listener = SharedPreferences.OnSharedPreferenceChangeListener { pref, impactedKey ->
        if (key == impactedKey) {
            resultState.value = pref.valueExtractor(key, null)
        }
    }

    DisposableEffect(key1 = key) {
        registerOnSharedPreferenceChangeListener(listener)
        onDispose {
            unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    return resultState
}
