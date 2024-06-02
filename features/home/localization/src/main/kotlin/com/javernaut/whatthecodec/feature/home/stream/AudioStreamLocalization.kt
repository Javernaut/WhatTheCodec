package com.javernaut.whatthecodec.feature.home.stream

import android.content.res.Resources
import com.javernaut.whatthecodec.feature.home.localization.R
import com.javernaut.whatthecodec.feature.settings.api.content.AudioStreamFeature
import io.github.javernaut.mediafile.AudioStream
import io.github.javernaut.mediafile.displayable.displayableLanguage
import io.github.javernaut.mediafile.displayable.getDisplayableDisposition
import io.github.javernaut.mediafile.displayable.toDisplayable

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
