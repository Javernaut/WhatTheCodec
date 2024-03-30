package com.javernaut.whatthecodec.presentation.settings

import android.content.Context
import android.net.Uri
import androidx.annotation.StringRes
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.compose.preference.ListPreference
import com.javernaut.whatthecodec.presentation.compose.preference.MultiSelectListPreference
import com.javernaut.whatthecodec.presentation.compose.preference.Preference
import com.javernaut.whatthecodec.presentation.compose.preference.PreferenceDivider
import com.javernaut.whatthecodec.presentation.compose.preference.PreferenceGroup
import com.javernaut.whatthecodec.presentation.compose.theme3.WhatTheCodecM3Theme

@Composable
fun SettingsScreen(goUp: () -> Unit) {
    val context = LocalContext.current
    SettingsScreen(openUrl = {
        openUrl(context, it)
    }, goUp)
}

private fun openUrl(context: Context, url: String) {
    CustomTabsIntent.Builder()
        .build()
        .launchUrl(context, Uri.parse(url))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    openUrl: (String) -> Unit,
    goUp: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SettingsTopAppBar(scrollBehavior, goUp)
        }
    ) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(it)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingsContent(openUrl)
        }
    }
}

@Composable
private fun ColumnScope.SettingsContent(openUrl: (String) -> Unit) {
    PreferenceGroup(title = R.string.settings_category_general) {
        ThemeSelectionPreference()
    }

    PreferenceGroup(title = R.string.settings_category_content) {
        PreferredVideoContentPreference()
        PreferenceDivider()
        PreferredAudioContentPreference()
        PreferenceDivider()
        PreferredSubtitlesContentPreference()
    }

    PreferenceGroup(title = R.string.settings_category_about) {
        OpenUrlPreference(
            title = R.string.settings_source_code_title,
            summary = R.string.settings_source_code_summary,
            openUrl = openUrl
        )
        PreferenceDivider()
        OpenUrlPreference(
            title = R.string.settings_privacy_policy_title,
            summary = R.string.settings_privacy_policy_summary,
            openUrl = openUrl
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsTopAppBar(scrollBehavior: TopAppBarScrollBehavior, goUp: () -> Unit) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.settings_title)) },
        navigationIcon = {
            IconButton(
                onClick = goUp
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.content_description_back)
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
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
private fun OpenUrlPreference(
    @StringRes title: Int,
    @StringRes summary: Int,
    openUrl: (String) -> Unit
) {
    val url = stringResource(id = summary)
    Preference(title = title, summary = summary) {
        openUrl(url)
    }
}

@PreviewLightDark
@Composable
private fun PreviewSettingsScreen() {
    WhatTheCodecM3Theme {
        SettingsScreen({}, {})
    }
}
