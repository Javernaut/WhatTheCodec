package com.javernaut.whatthecodec.home.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.javernaut.whatthecodec.home.data.model.AudioStreamFeature
import com.javernaut.whatthecodec.home.data.model.SubtitleStreamFeature
import com.javernaut.whatthecodec.home.data.model.VideoStreamFeature
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StreamFeatureRepository @Inject constructor(
    @ApplicationContext context: Context
) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "stream_features")

    private val dataStore = context.dataStore

    private val keyVideoStreamFeatures = stringSetPreferencesKey("video_stream_features")
    private val keyAudioStreamFeatures = stringSetPreferencesKey("audio_stream_features")
    private val keySubtitleStreamFeatures = stringSetPreferencesKey("subtitle_stream_features")

    val videoStreamFeatures: Flow<Set<VideoStreamFeature>> =
        context.dataStore.enumSetFlow(
            keyVideoStreamFeatures
        )

    val audioStreamFeatures: Flow<Set<AudioStreamFeature>> =
        context.dataStore.enumSetFlow(
            keyAudioStreamFeatures
        )

    val subtitleStreamFeatures: Flow<Set<SubtitleStreamFeature>> =
        context.dataStore.enumSetFlow(
            keySubtitleStreamFeatures
        )


    suspend fun setVideoStreamFeatures(newVideoStreamFeatures: Set<VideoStreamFeature>) {
        dataStore.saveEnumSet(keyVideoStreamFeatures, newVideoStreamFeatures)
    }

    suspend fun setAudioStreamFeatures(newAudioStreamFeatures: Set<AudioStreamFeature>) {
        dataStore.saveEnumSet(keyAudioStreamFeatures, newAudioStreamFeatures)
    }

    suspend fun setSubtitleStreamFeatures(newSubtitleStreamFeatures: Set<SubtitleStreamFeature>) {
        dataStore.saveEnumSet(keySubtitleStreamFeatures, newSubtitleStreamFeatures)
    }
}
