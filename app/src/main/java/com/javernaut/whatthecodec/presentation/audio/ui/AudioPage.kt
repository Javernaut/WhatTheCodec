package com.javernaut.whatthecodec.presentation.audio.ui

import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.stream.adapter.TempStreamFeaturesGrid
import com.javernaut.whatthecodec.presentation.subtitle.ui.SimplePage
import com.javernaut.whatthecodec.presentation.subtitle.ui.TempStreamFeature
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
        // TODO extract SimpleCardContent?
        // TODO merge some part into the SimplePage?
        AudioCardContent(item, itemModifier)
    }
}

@Composable
fun AudioCardContent(
    stream: AudioStream,
    modifier: Modifier = Modifier
) {
    // TODO Reflect changes from SubtitleCardContent
    val streamFeatures = AudioFeature.values().toList()
    TempStreamFeaturesGrid(
        stream,
        streamFeatures,
        modifier
    )
}

enum class AudioFeature(
    @StringRes override val key: Int,
    @StringRes override val title: Int
) :
    TempStreamFeature<AudioStream> {

    CODEC(
        key = R.string.settings_content_codec,
        title = R.string.page_audio_codec_name
    ) {
        override fun getValue(stream: AudioStream, resources: Resources): String {
            return stream.basicInfo.codecName
        }
    },
    BITRATE(
        key = R.string.settings_content_bitrate,
        title = R.string.page_audio_bit_rate
    ) {
        override fun getValue(stream: AudioStream, resources: Resources): String? {
            return stream.bitRate.toDisplayable(resources)
        }
    },
    CHANNELS(
        key = R.string.settings_content_audio_channels,
        title = R.string.page_audio_channels
    ) {
        override fun getValue(stream: AudioStream, resources: Resources): String {
            return stream.channels.toString()
        }
    },
    CHANNEL_LAYOUT(
        key = R.string.settings_content_audio_channel_layout,
        title = R.string.page_audio_channel_layout
    ) {
        override fun getValue(stream: AudioStream, resources: Resources): String? {
            return stream.channelLayout
        }
    },
    SAMPLE_FORMAT(
        key = R.string.settings_content_audio_sample_format,
        title = R.string.page_audio_sample_format
    ) {
        override fun getValue(stream: AudioStream, resources: Resources): String? {
            return stream.sampleFormat
        }
    },
    SAMPLE_RATE(
        key = R.string.settings_content_audio_sample_rate,
        title = R.string.page_audio_sample_rate
    ) {
        override fun getValue(stream: AudioStream, resources: Resources): String? {
            return stream.sampleRate.toDisplayable(resources)
        }
    },
    LANGUAGE(
        key = R.string.settings_content_language,
        title = R.string.page_stream_language
    ) {
        override fun getValue(stream: AudioStream, resources: Resources): String? {
            return stream.basicInfo.displayableLanguage
        }
    },
    DISPOSITION(
        key = R.string.settings_content_disposition,
        title = R.string.page_stream_disposition
    ) {
        override fun getValue(stream: AudioStream, resources: Resources): String? {
            return stream.basicInfo.getDisplayableDisposition(resources)
        }
    };
}
