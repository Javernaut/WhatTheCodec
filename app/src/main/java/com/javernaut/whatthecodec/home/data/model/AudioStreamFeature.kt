package com.javernaut.whatthecodec.home.data.model

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
