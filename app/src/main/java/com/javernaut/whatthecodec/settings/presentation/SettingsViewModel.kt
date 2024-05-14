package com.javernaut.whatthecodec.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javernaut.whatthecodec.home.data.StreamFeatureRepository
import com.javernaut.whatthecodec.home.data.model.AudioStreamFeature
import com.javernaut.whatthecodec.home.data.model.SubtitleStreamFeature
import com.javernaut.whatthecodec.home.data.model.VideoStreamFeature
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val streamFeatureRepository: StreamFeatureRepository
) : ViewModel() {
    val videoStreamFeatures = streamFeatureRepository.videoStreamFeatures.stateIn(emptySet())

    val audioStreamFeatures = streamFeatureRepository.audioStreamFeatures.stateIn(emptySet())

    val subtitleStreamFeatures = streamFeatureRepository.subtitleStreamFeatures.stateIn(emptySet())

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

context(ViewModel)
fun <T> Flow<T>.stateIn(initialValue: T): StateFlow<T> {
    return stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue
    )
}
