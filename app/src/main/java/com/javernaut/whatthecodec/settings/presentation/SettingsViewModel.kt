package com.javernaut.whatthecodec.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javernaut.whatthecodec.feature.settings.api.content.AudioStreamFeature
import com.javernaut.whatthecodec.feature.settings.api.content.ContentSettingsRepository
import com.javernaut.whatthecodec.feature.settings.api.content.SubtitleStreamFeature
import com.javernaut.whatthecodec.feature.settings.api.content.VideoStreamFeature
import com.javernaut.whatthecodec.feature.settings.api.theme.AppTheme
import com.javernaut.whatthecodec.feature.settings.api.theme.ThemeSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val contentSettingsRepository: ContentSettingsRepository,
    private val themeSettingsRepository: ThemeSettingsRepository
) : ViewModel() {
    val videoStreamFeatures = contentSettingsRepository.videoStreamFeatures.stateIn(emptySet())

    val audioStreamFeatures = contentSettingsRepository.audioStreamFeatures.stateIn(emptySet())

    val subtitleStreamFeatures =
        contentSettingsRepository.subtitleStreamFeatures.stateIn(emptySet())

    fun setVideoStreamFeatures(newVideoStreamFeatures: Set<VideoStreamFeature>) =
        viewModelScope.launch {
            contentSettingsRepository.setVideoStreamFeatures(newVideoStreamFeatures)
        }

    fun setAudioStreamFeatures(newAudioStreamFeatures: Set<AudioStreamFeature>) =
        viewModelScope.launch {
            contentSettingsRepository.setAudioStreamFeatures(newAudioStreamFeatures)
        }

    fun setSubtitleStreamFeatures(newSubtitleStreamFeatures: Set<SubtitleStreamFeature>) =
        viewModelScope.launch {
            contentSettingsRepository.setSubtitleStreamFeatures(newSubtitleStreamFeatures)
        }

    val appTheme = themeSettingsRepository.themeSettings
        .map { it.selectedTheme }
        .stateIn(AppTheme.Auto)

    fun setAppTheme(newAppTheme: AppTheme) {
        viewModelScope.launch {
            themeSettingsRepository.setSelectedTheme(newAppTheme)
        }
    }
}

context(ViewModel)
fun <T> Flow<T>.stateIn(initialValue: T): StateFlow<T> {
    return stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue
    )
}
