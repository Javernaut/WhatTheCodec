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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.compose.preference.ListPreference
import com.javernaut.whatthecodec.compose.preference.MultiSelectListPreference
import com.javernaut.whatthecodec.compose.preference.Preference
import com.javernaut.whatthecodec.compose.preference.PreferenceDivider
import com.javernaut.whatthecodec.compose.preference.PreferenceGroup
import com.javernaut.whatthecodec.compose.theme.WhatTheCodecTheme
import com.javernaut.whatthecodec.compose.theme.dynamic.AppTheme
import com.javernaut.whatthecodec.compose.theme.dynamic.ThemeViewModel
import com.javernaut.whatthecodec.home.data.AudioStreamFeature
import com.javernaut.whatthecodec.home.data.SubtitleStreamFeature
import com.javernaut.whatthecodec.home.data.VideoStreamFeature
import com.javernaut.whatthecodec.home.ui.audio.displayableResource
import com.javernaut.whatthecodec.home.ui.subtitle.displayableResource
import com.javernaut.whatthecodec.home.ui.video.displayableResource
import com.javernaut.whatthecodec.settings.presentation.SettingsViewModel

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
private fun ThemeSelectionPreference(
    themeViewModel: ThemeViewModel = viewModel()
) {
    val selectedTheme by themeViewModel.appTheme.collectAsState()
    val selectedThemeIndex = AppTheme.entries.indexOf(selectedTheme)
    ListPreference(
        title = stringResource(id = R.string.settings_theme_title),
        displayableEntries = stringArrayResource(id = R.array.settings_theme_entries).toList(),
        selectedItemIndex = selectedThemeIndex
    ) {
        themeViewModel.setAppTheme(
            AppTheme.entries[it]
        )
    }
}

@Composable
private fun PreferredVideoContentPreference(
    viewModel: SettingsViewModel = viewModel()
) {
    val selectedItems by viewModel.videoStreamFeatures.collectAsState()
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
    viewModel: SettingsViewModel = viewModel()
) {
    val selectedItems by viewModel.audioStreamFeatures.collectAsState()
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
    viewModel: SettingsViewModel = viewModel()
) {
    val selectedItems by viewModel.subtitleStreamFeatures.collectAsState()
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
        SettingsScreen({}, {})
    }
}
