package com.javernaut.whatthecodec.presentation.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.compose.common.WtcTopAppBar
import com.javernaut.whatthecodec.presentation.compose.preference.ListPreference
import com.javernaut.whatthecodec.presentation.compose.preference.MultiSelectListPreference
import com.javernaut.whatthecodec.presentation.compose.preference.Preference
import com.javernaut.whatthecodec.presentation.compose.preference.PreferenceDivider
import com.javernaut.whatthecodec.presentation.compose.preference.PreferenceTitle
import com.javernaut.whatthecodec.presentation.compose.theme3.WhatTheCodecM3Theme

class SettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        setContent {
            WhatTheCodecM3Theme {
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
            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(it)
            ) {
                SettingsContent()
            }
        }
    }

    @Composable
    private fun SettingsContent() {
        PreferenceTitle(title = R.string.settings_category_general)
        ThemeSelectionPreference()

        PreferenceDivider()

        PreferenceTitle(title = R.string.settings_category_content)
        PreferredVideoContentPreference()
        PreferredAudioContentPreference()
        PreferredSubtitlesContentPreference()

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
        WtcTopAppBar(
            title = { Text(text = stringResource(id = R.string.settings_title)) },
            navigationIcon = {
                IconButton(onClick = {
                    // TODO Navigate up with nav controller instead
                    finish()
                }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.content_description_back)
                    )
                }
            })
    }

    @Composable
    private fun ThemeSelectionPreference() {
        ListPreference(
            "theme",
            defaultValue = stringResource(id = R.string.settings_theme_default_pref_value),
            title = stringResource(id = R.string.settings_theme_title),
            displayableEntries = stringArrayResource(id = R.array.settings_theme_entries).toList(),
            entriesCodes = stringArrayResource(id = R.array.settings_theme_entryValues).toList()
        ) {
            ThemeManager.setNightModePreference(it)
        }
    }

    @Composable
    private fun PreferredVideoContentPreference() {
        val entriesCodes =
            stringArrayResource(id = R.array.settings_content_video_entryValues).toList()
        MultiSelectListPreference(
            PreferencesKeys.VIDEO,
            defaultValue = entriesCodes.toSet(),
            title = stringResource(id = R.string.settings_content_video_title),
            displayableEntries = stringArrayResource(id = R.array.settings_content_video_entries).toList(),
            entriesCodes = entriesCodes
        )
    }

    @Composable
    private fun PreferredAudioContentPreference() {
        val entriesCodes =
            stringArrayResource(id = R.array.settings_content_audio_entryValues).toList()
        MultiSelectListPreference(
            PreferencesKeys.AUDIO,
            defaultValue = entriesCodes.toSet(),
            title = stringResource(id = R.string.settings_content_audio_title),
            displayableEntries = stringArrayResource(id = R.array.settings_content_audio_entries).toList(),
            entriesCodes = entriesCodes
        )
    }

    @Composable
    private fun PreferredSubtitlesContentPreference() {
        val entriesCodes =
            stringArrayResource(id = R.array.settings_content_subtitles_entryValues).toList()
        MultiSelectListPreference(
            PreferencesKeys.SUBTITLES,
            defaultValue = entriesCodes.toSet(),
            title = stringResource(id = R.string.settings_content_subtitles_title),
            displayableEntries = stringArrayResource(id = R.array.settings_content_subtitles_entries).toList(),
            entriesCodes = entriesCodes
        )
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
