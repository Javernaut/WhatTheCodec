package com.javernaut.whatthecodec.home.data.model

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
