package com.javernaut.whatthecodec.presentation.audio.ui

import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.stream.adapter.StreamFeaturesGrid
import com.javernaut.whatthecodec.presentation.stream.model.StreamFeature
import com.javernaut.whatthecodec.presentation.stream.model.makeStream
import com.javernaut.whatthecodec.presentation.subtitle.ui.SimplePage
import io.github.javernaut.mediafile.AudioStream
import io.github.javernaut.mediafile.displayable.toDisplayable

private fun convertStream(audioStream: AudioStream, resources: Resources) =
    makeStream(audioStream.basicInfo, resources) {
        add(StreamFeature(R.string.page_audio_codec_name, audioStream.basicInfo.codecName))

        audioStream.bitRate.toDisplayable(resources)?.let {
            add(StreamFeature(R.string.page_audio_bit_rate, it))
        }

        add(StreamFeature(R.string.page_audio_channels, audioStream.channels.toString()))

        audioStream.channelLayout?.let {
            add(StreamFeature(R.string.page_audio_channel_layout, it))
        }
        audioStream.sampleFormat?.let {
            add(StreamFeature(R.string.page_audio_sample_format, it))
        }

        audioStream.sampleRate.toDisplayable(resources)?.let {
            add(StreamFeature(R.string.page_audio_sample_rate, it))
        }
    }

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
fun AudioCardContent(
    stream: AudioStream,
    modifier: Modifier = Modifier
) {
    val convertedStream = convertStream(stream, LocalContext.current.resources)
    StreamFeaturesGrid(
        features = convertedStream,
        modifier = modifier
    )
}
