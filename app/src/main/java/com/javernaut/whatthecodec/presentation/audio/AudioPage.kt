package com.javernaut.whatthecodec.presentation.audio

import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.settings.PreferencesKeys
import com.javernaut.whatthecodec.presentation.stream.SimplePage
import com.javernaut.whatthecodec.presentation.stream.StreamFeature
import com.javernaut.whatthecodec.presentation.stream.StreamFeaturesGrid
import com.javernaut.whatthecodec.presentation.stream.getFilteredStreamFeatures
import io.github.javernaut.mediafile.AudioStream
import io.github.javernaut.mediafile.displayable.displayableLanguage
import io.github.javernaut.mediafile.displayable.getDisplayableDisposition
import io.github.javernaut.mediafile.displayable.toDisplayable

@Composable
fun AudioPage(
    streams: List<AudioStream>,
    modifier: Modifier = Modifier
) {
    SimplePage(streams, modifier) { item, itemModifier ->
        AudioCardContent(item, itemModifier)
    }
}

@Composable
private fun AudioCardContent(
    stream: AudioStream,
    modifier: Modifier = Modifier
) {
    val streamFeatures = getFilteredStreamFeatures(
        defaultValueResId = R.array.settings_content_audio_entryValues,
        preferenceKey = PreferencesKeys.AUDIO,
        allValues = AudioFeature.values().toList()
    )

    StreamFeaturesGrid(
        stream,
        streamFeatures,
        modifier
    )
}

private enum class AudioFeature(
    @StringRes override val key: Int,
    @StringRes override val title: Int
) :
    StreamFeature<AudioStream> {

    CODEC(
        key = R.string.settings_content_codec,
        title = R.string.page_audio_codec_name
    ) {
        override fun getValue(stream: AudioStream, resources: Resources) =
            stream.basicInfo.codecName
    },
    BITRATE(
        key = R.string.settings_content_bitrate,
        title = R.string.page_audio_bit_rate
    ) {
        override fun getValue(stream: AudioStream, resources: Resources) =
            stream.bitRate.toDisplayable(resources)
    },
    CHANNELS(
        key = R.string.settings_content_audio_channels,
        title = R.string.page_audio_channels
    ) {
        override fun getValue(stream: AudioStream, resources: Resources) =
            stream.channels.toString()
    },
    CHANNEL_LAYOUT(
        key = R.string.settings_content_audio_channel_layout,
        title = R.string.page_audio_channel_layout
    ) {
        override fun getValue(stream: AudioStream, resources: Resources) =
            stream.channelLayout
    },
    SAMPLE_FORMAT(
        key = R.string.settings_content_audio_sample_format,
        title = R.string.page_audio_sample_format
    ) {
        override fun getValue(stream: AudioStream, resources: Resources) =
            stream.sampleFormat
    },
    SAMPLE_RATE(
        key = R.string.settings_content_audio_sample_rate,
        title = R.string.page_audio_sample_rate
    ) {
        override fun getValue(stream: AudioStream, resources: Resources) =
            stream.sampleRate.toDisplayable(resources)
    },
    LANGUAGE(
        key = R.string.settings_content_language,
        title = R.string.page_stream_language
    ) {
        override fun getValue(stream: AudioStream, resources: Resources) =
            stream.basicInfo.displayableLanguage
    },
    DISPOSITION(
        key = R.string.settings_content_disposition,
        title = R.string.page_stream_disposition
    ) {
        override fun getValue(stream: AudioStream, resources: Resources) =
            stream.basicInfo.getDisplayableDisposition(resources)
    };
}
