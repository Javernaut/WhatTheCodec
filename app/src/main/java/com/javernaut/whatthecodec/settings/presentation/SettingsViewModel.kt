package com.javernaut.whatthecodec.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javernaut.whatthecodec.home.data.AudioStreamFeature
import com.javernaut.whatthecodec.home.data.StreamFeatureRepository
import com.javernaut.whatthecodec.home.data.SubtitleStreamFeature
import com.javernaut.whatthecodec.home.data.VideoStreamFeature
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val streamFeatureRepository: StreamFeatureRepository
) : ViewModel() {
    val videoStreamFeatures = streamFeatureRepository.videoStreamFeatures.stateIn(
        viewModelScope, SharingStarted.Eagerly, emptySet()
    )

    val audioStreamFeatures = streamFeatureRepository.audioStreamFeatures.stateIn(
        viewModelScope, SharingStarted.Eagerly, emptySet()
    )

    val subtitleStreamFeatures = streamFeatureRepository.subtitleStreamFeatures.stateIn(
        viewModelScope, SharingStarted.Eagerly, emptySet()
    )

    fun setVideoStreamFeatures(newVideoStreamFeatures: Set<VideoStreamFeature>) =
        viewModelScope.launch {
            streamFeatureRepository.setVideoStreamFeatures(newVideoStreamFeatures)
        }

    fun setAudioStreamFeatures(newAudioStreamFeatures: Set<AudioStreamFeature>) =
        viewModelScope.launch {
            streamFeatureRepository.setAudioStreamFeatures(newAudioStreamFeatures)
        }

    fun setSubtitleStreamFeatures(newSubtitleStreamFeatures: Set<SubtitleStreamFeature>) =
        viewModelScope.launch {
            streamFeatureRepository.setSubtitleStreamFeatures(newSubtitleStreamFeatures)
        }
}
