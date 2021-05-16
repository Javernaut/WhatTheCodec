package com.javernaut.whatthecodec.presentation.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.preference.PreferenceManager
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.compose.theme.WhatTheCodecTheme
import com.javernaut.whatthecodec.presentation.compose.theme.secondaryText

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setContent {
            WhatTheCodecTheme {
                SettingsScreen()
            }
        }
    }

    private fun openUrl(url: String) {
        CustomTabsIntent.Builder()
            .build()
            .launchUrl(this, Uri.parse(url))
    }

    @Composable
    fun SettingsScreen() {
        Scaffold(
            topBar = {
                SettingsTopAppBar()
            }
        ) {
            Column(Modifier.padding(it)) {
                SettingsContent()
            }
        }
    }

    @Composable
    private fun SettingsContent() {
        PreferenceTitle(title = R.string.settings_category_general)
        ThemeSelectionPreference()
        PreferenceDivider()
        PreferenceTitle(title = R.string.settings_category_about)
        OpenUrlPreference(
            title = R.string.settings_source_code_title,
            summary = R.string.settings_source_code_summary
        )
        OpenUrlPreference(
            title = R.string.settings_privacy_policy_title,
            summary = R.string.settings_privacy_policy_summary
        )
    }

    @Composable
    private fun SettingsTopAppBar() {
        TopAppBar(
            elevation = if (isSystemInDarkTheme()) 0.dp else AppBarDefaults.TopAppBarElevation,
            title = { Text(text = stringResource(id = R.string.settings_title)) },
            navigationIcon = {
                IconButton(onClick = { onSupportNavigateUp() }) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = stringResource(id = R.string.content_description_back)
                    )
                }
            })
    }

    @Composable
    private fun PreferenceTitle(@StringRes title: Int) {
        Text(
            stringResource(id = title),
            Modifier
                .fillMaxWidth()
                .padding(start = 72.dp, top = 24.dp, end = 8.dp, bottom = 8.dp),
            style = MaterialTheme.typography.subtitle2,
            color = MaterialTheme.colors.secondary
        )
    }

    @Composable
    private fun ThemeSelectionPreference() {
        val applicationContext = LocalContext.current.applicationContext
        val defaultSharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val currentNightMode = defaultSharedPreferences
            .getString("theme", stringResource(id = R.string.settings_theme_default_pref_value))

        val humanReadableNightModes = stringArrayResource(id = R.array.settings_theme_entries)
        val modeCodes = stringArrayResource(id = R.array.settings_theme_entryValues)
        val currentlySelectedModeIndex = modeCodes.indexOf(currentNightMode)

        var dialogOpened by remember { mutableStateOf(false) }
        val closeDialog = { dialogOpened = false }
        Preference(
            title = stringResource(id = R.string.settings_theme_title),
            summary = humanReadableNightModes[currentlySelectedModeIndex]
        ) {
            dialogOpened = true
        }

        if (dialogOpened) {
            SingleChoicePreferenceDialog(
                title = stringResource(id = R.string.settings_theme_title),
                onDismissRequest = closeDialog,
                items = humanReadableNightModes.toList(),
                currentlySelectedModeIndex = currentlySelectedModeIndex
            ) {
                val newValueToSet = modeCodes[it]
                defaultSharedPreferences
                    .edit()
                    .putString("theme", newValueToSet)
                    .apply()
                ThemeManager.setNightModePreference(newValueToSet)
            }
        }
    }

    @Composable
    private fun Preference(
        @StringRes title: Int,
        @StringRes summary: Int,
        clickHandler: () -> Unit
    ) =
        Preference(
            title = stringResource(id = title),
            summary = stringResource(id = summary),
            clickHandler = clickHandler
        )

    @Composable
    private fun Preference(
        title: String,
        summary: String,
        clickHandler: () -> Unit
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .clickable(onClick = clickHandler)
                .padding(
                    start = 72.dp, top = 16.dp, end = 8.dp, bottom = 16.dp
                )
        ) {
            Text(
                title,
                style = MaterialTheme.typography.subtitle1
            )
            Text(
                summary,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.secondaryText
            )
        }
    }

    @Composable
    private fun OpenUrlPreference(@StringRes title: Int, @StringRes summary: Int) {
        val url = stringResource(id = summary)
        Preference(title = title, summary = summary) {
            openUrl(url)
        }
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, SettingsActivity::class.java))
        }
    }
}
