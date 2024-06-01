package com.javernaut.whatthecodec.feature.settings.api.content

import kotlinx.coroutines.flow.Flow

interface ContentSettingsRepository {
    val videoStreamFeatures: Flow<Set<VideoStreamFeature>>
    val audioStreamFeatures: Flow<Set<AudioStreamFeature>>
    val subtitleStreamFeatures: Flow<Set<SubtitleStreamFeature>>

    suspend fun setVideoStreamFeatures(newVideoStreamFeatures: Set<VideoStreamFeature>)

    suspend fun setAudioStreamFeatures(newAudioStreamFeatures: Set<AudioStreamFeature>)

    suspend fun setSubtitleStreamFeatures(newSubtitleStreamFeatures: Set<SubtitleStreamFeature>)
}
