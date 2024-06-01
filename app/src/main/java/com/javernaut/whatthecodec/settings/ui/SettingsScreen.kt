package com.javernaut.whatthecodec.settings.ui

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.compose.preference.ListPreference
import com.javernaut.whatthecodec.compose.preference.MultiSelectListPreference
import com.javernaut.whatthecodec.compose.preference.Preference
import com.javernaut.whatthecodec.compose.preference.PreferenceDivider
import com.javernaut.whatthecodec.compose.preference.PreferenceGroup
import com.javernaut.whatthecodec.compose.theme.WhatTheCodecTheme
import com.javernaut.whatthecodec.feature.settings.api.content.AudioStreamFeature
import com.javernaut.whatthecodec.feature.settings.api.content.SubtitleStreamFeature
import com.javernaut.whatthecodec.feature.settings.api.content.VideoStreamFeature
import com.javernaut.whatthecodec.feature.settings.api.theme.AppTheme
import com.javernaut.whatthecodec.home.ui.audio.displayableResource
import com.javernaut.whatthecodec.home.ui.subtitle.displayableResource
import com.javernaut.whatthecodec.home.ui.video.displayableResource
import com.javernaut.whatthecodec.settings.presentation.SettingsViewModel

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    SettingsScreen(
        settingsViewModel = settingsViewModel,
        openUrl = {
            openUrl(context, it)
        },
        onBackClick = onBackClick
    )
}

private fun openUrl(context: Context, url: String) {
    CustomTabsIntent.Builder()
        .build()
        .launchUrl(context, Uri.parse(url))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    openUrl: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SettingsTopAppBar(scrollBehavior, onBackClick)
        }
    ) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(it)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingsContent(settingsViewModel, openUrl)
        }
    }
}

@Composable
private fun ColumnScope.SettingsContent(
    settingsViewModel: SettingsViewModel,
    openUrl: (String) -> Unit
) {
    PreferenceGroup(title = R.string.settings_category_general) {
        ThemeSelectionPreference(settingsViewModel)
    }

    PreferenceGroup(title = R.string.settings_category_content) {
        PreferredVideoContentPreference(settingsViewModel)
        PreferenceDivider()
        PreferredAudioContentPreference(settingsViewModel)
        PreferenceDivider()
        PreferredSubtitlesContentPreference(settingsViewModel)
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
private fun SettingsTopAppBar(scrollBehavior: TopAppBarScrollBehavior, onBackClick: () -> Unit) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.settings_title)) },
        navigationIcon = {
            IconButton(
                onClick = onBackClick
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
private fun ThemeSelectionPreference(
    settingsViewModel: SettingsViewModel
) {
    val selectedTheme by settingsViewModel.appTheme.collectAsStateWithLifecycle()
    val selectedThemeIndex = AppTheme.entries.indexOf(selectedTheme)
    ListPreference(
        title = stringResource(id = R.string.settings_theme_title),
        displayableEntries = stringArrayResource(id = R.array.settings_theme_entries).toList(),
        selectedItemIndex = selectedThemeIndex
    ) {
        settingsViewModel.setAppTheme(
            AppTheme.entries[it]
        )
    }
}

@Composable
private fun PreferredVideoContentPreference(
    viewModel: SettingsViewModel
) {
    val selectedItems by viewModel.videoStreamFeatures.collectAsStateWithLifecycle()
    MultiSelectListPreference(
        title = stringResource(id = R.string.settings_content_video_title),
        items = VideoStreamFeature.entries.map {
            stringResource(id = it.displayableResource)
        },
        currentlySelectedIndexes = selectedItems.map { it.ordinal },
    ) { newItemsPositions ->
        viewModel.setVideoStreamFeatures(
            newItemsPositions.mapTo(mutableSetOf()) {
                VideoStreamFeature.entries[it]
            }
        )
    }
}

@Composable
private fun PreferredAudioContentPreference(
    viewModel: SettingsViewModel
) {
    val selectedItems by viewModel.audioStreamFeatures.collectAsStateWithLifecycle()
    MultiSelectListPreference(
        title = stringResource(id = R.string.settings_content_audio_title),
        items = AudioStreamFeature.entries.map {
            stringResource(id = it.displayableResource)
        },
        currentlySelectedIndexes = selectedItems.map { it.ordinal },
    ) { newItemsPositions ->
        viewModel.setAudioStreamFeatures(
            newItemsPositions.mapTo(mutableSetOf()) {
                AudioStreamFeature.entries[it]
            }
        )
    }
}

@Composable
private fun PreferredSubtitlesContentPreference(
    viewModel: SettingsViewModel
) {
    val selectedItems by viewModel.subtitleStreamFeatures.collectAsStateWithLifecycle()
    MultiSelectListPreference(
        title = stringResource(id = R.string.settings_content_subtitles_title),
        items = SubtitleStreamFeature.entries.map {
            stringResource(id = it.displayableResource)
        },
        currentlySelectedIndexes = selectedItems.map { it.ordinal },
    ) { newItemsPositions ->
        viewModel.setSubtitleStreamFeatures(
            newItemsPositions.mapTo(mutableSetOf()) {
                SubtitleStreamFeature.entries[it]
            }
        )
    }
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
    WhatTheCodecTheme.Static {
        SettingsScreen(openUrl = {}, onBackClick = {})
    }
}
