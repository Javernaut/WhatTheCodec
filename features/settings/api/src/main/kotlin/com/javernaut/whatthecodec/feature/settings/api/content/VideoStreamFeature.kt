package com.javernaut.whatthecodec.feature.settings.api.content

import androidx.annotation.Keep

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
