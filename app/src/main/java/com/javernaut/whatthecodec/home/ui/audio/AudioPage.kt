package com.javernaut.whatthecodec.home.ui.audio

import android.content.res.Resources
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.feature.settings.api.content.AudioStreamFeature
import com.javernaut.whatthecodec.home.presentation.model.AudioPage
import com.javernaut.whatthecodec.home.ui.stream.DisplayableStreamFeature
import com.javernaut.whatthecodec.home.ui.stream.SimplePage
import com.javernaut.whatthecodec.home.ui.stream.StreamFeaturesGrid
import io.github.javernaut.mediafile.AudioStream
import io.github.javernaut.mediafile.displayable.displayableLanguage
import io.github.javernaut.mediafile.displayable.getDisplayableDisposition
import io.github.javernaut.mediafile.displayable.toDisplayable

@Composable
fun AudioPage(
    audioPage: AudioPage,
    contentPadding: PaddingValues,
    onCopyValue: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    SimplePage(audioPage.streams, contentPadding, modifier) { item, itemModifier ->
        AudioCardContent(item, audioPage.streamFeatures, onCopyValue, itemModifier)
    }
}

@Composable
private fun AudioCardContent(
    stream: AudioStream,
    streamFeatures: Set<AudioStreamFeature>,
    onCopyValue: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val resources = LocalContext.current.resources
    val displayableStreamFeatures =
        AudioStreamFeature.entries.filter {
            streamFeatures.contains(it)
        }.mapNotNull {
            it.toDisplayableStreamFeature(stream, resources)
        }

    StreamFeaturesGrid(
        displayableStreamFeatures,
        onCopyValue,
        modifier
    )
}

fun AudioStreamFeature.toDisplayableStreamFeature(
    stream: AudioStream,
    resources: Resources
) = when (this) {
    AudioStreamFeature.Codec -> stream.basicInfo.codecName
    AudioStreamFeature.Bitrate -> stream.bitRate.toDisplayable(resources)
    AudioStreamFeature.Channels -> stream.channels.toString()
    AudioStreamFeature.ChannelLayout -> stream.channelLayout
    AudioStreamFeature.SampleFormat -> stream.sampleFormat
    AudioStreamFeature.SampleRate -> stream.sampleRate.toDisplayable(resources)
    AudioStreamFeature.Language -> stream.basicInfo.displayableLanguage
    AudioStreamFeature.Disposition -> stream.basicInfo.getDisplayableDisposition(resources)
}?.let {
    DisplayableStreamFeature(
        name = resources.getString(displayableResource),
        value = it
    )
}

val AudioStreamFeature.displayableResource: Int
    get() = when (this) {
        AudioStreamFeature.Codec -> R.string.page_audio_codec_name
        AudioStreamFeature.Bitrate -> R.string.page_audio_bit_rate
        AudioStreamFeature.Channels -> R.string.page_audio_channels
        AudioStreamFeature.ChannelLayout -> R.string.page_audio_channel_layout
        AudioStreamFeature.SampleFormat -> R.string.page_audio_sample_format
        AudioStreamFeature.SampleRate -> R.string.page_audio_sample_rate
        AudioStreamFeature.Language -> R.string.page_stream_language
        AudioStreamFeature.Disposition -> R.string.page_stream_disposition
    }
