package com.javernaut.whatthecodec.feature.settings.data.content

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.javernaut.whatthecodec.feature.settings.api.content.AudioStreamFeature
import com.javernaut.whatthecodec.feature.settings.api.content.ContentSettingsRepository
import com.javernaut.whatthecodec.feature.settings.api.content.SubtitleStreamFeature
import com.javernaut.whatthecodec.feature.settings.api.content.VideoStreamFeature
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DefaultContentSettingsRepository @Inject constructor(
    @ApplicationContext context: Context
) : ContentSettingsRepository {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "stream_features")

    private val dataStore = context.dataStore

    private val keyVideoStreamFeatures = stringSetPreferencesKey("video_stream_features")
    private val keyAudioStreamFeatures = stringSetPreferencesKey("audio_stream_features")
    private val keySubtitleStreamFeatures = stringSetPreferencesKey("subtitle_stream_features")

    override val videoStreamFeatures: Flow<Set<VideoStreamFeature>> =
        context.dataStore.enumSetFlow(
            keyVideoStreamFeatures
        )

    override val audioStreamFeatures: Flow<Set<AudioStreamFeature>> =
        context.dataStore.enumSetFlow(
            keyAudioStreamFeatures
        )

    override val subtitleStreamFeatures: Flow<Set<SubtitleStreamFeature>> =
        context.dataStore.enumSetFlow(
            keySubtitleStreamFeatures
        )

    override suspend fun setVideoStreamFeatures(newVideoStreamFeatures: Set<VideoStreamFeature>) {
        dataStore.saveEnumSet(keyVideoStreamFeatures, newVideoStreamFeatures)
    }

    override suspend fun setAudioStreamFeatures(newAudioStreamFeatures: Set<AudioStreamFeature>) {
        dataStore.saveEnumSet(keyAudioStreamFeatures, newAudioStreamFeatures)
    }

    override suspend fun setSubtitleStreamFeatures(newSubtitleStreamFeatures: Set<SubtitleStreamFeature>) {
        dataStore.saveEnumSet(keySubtitleStreamFeatures, newSubtitleStreamFeatures)
    }
}
