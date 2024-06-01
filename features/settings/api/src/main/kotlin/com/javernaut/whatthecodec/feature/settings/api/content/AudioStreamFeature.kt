package com.javernaut.whatthecodec.feature.settings.api.content

import androidx.annotation.Keep

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
