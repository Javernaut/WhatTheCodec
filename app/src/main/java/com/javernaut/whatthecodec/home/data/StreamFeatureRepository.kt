package com.javernaut.whatthecodec.home.data

import android.content.Context
import androidx.annotation.Keep
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.EnumSet
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
        dataStore.saveEnumSet(keyAudioStreamFeatures, newSubtitleStreamFeatures)
    }
}

private inline fun <reified E : Enum<E>> DataStore<Preferences>.enumSetFlow(
    key: Preferences.Key<Set<String>>
): Flow<Set<E>> = data.map {
    it[key]?.mapTo(
        // Mapping into an EnumSet, as more efficient set implementation for enums
        mutableEnumSet()
    ) {
        enumValueOf(it)
    }
    // If nothing is present in DataStore, we treat it as 'everything is enabled'
        ?: completeEnumSet()
}

private suspend fun DataStore<Preferences>.saveEnumSet(
    key: Preferences.Key<Set<String>>,
    enumSet: Set<Any>
) {
    edit { settings ->
        settings[key] = enumSet.mapTo(
            mutableSetOf()
        ) {
            it.toString()
        }
    }
}

inline fun <reified E : Enum<E>> mutableEnumSet(): EnumSet<E> {
    return EnumSet.noneOf(E::class.java)
}

inline fun <reified E : Enum<E>> completeEnumSet(): EnumSet<E> {
    return EnumSet.allOf(E::class.java)
}

@Keep
enum class VideoStreamFeature {
    Codec,
    Bitrate,
    FrameRate,
    FrameWidth,
    FrameHeight,
    Language,
    Disposition
}

@Keep
enum class AudioStreamFeature {
    Codec,
    Bitrate,
    Channels,
    ChannelLayout,
    SampleFormat,
    SampleRate,
    Language,
    Disposition
}

@Keep
enum class SubtitleStreamFeature {
    Codec,
    Language,
    Disposition
}
