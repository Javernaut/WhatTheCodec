package com.javernaut.whatthecodec.feature.home.stream

import android.content.res.Resources
import com.javernaut.whatthecodec.feature.home.localization.R
import com.javernaut.whatthecodec.feature.settings.api.content.VideoStreamFeature
import io.github.javernaut.mediafile.VideoStream
import io.github.javernaut.mediafile.displayable.displayableLanguage
import io.github.javernaut.mediafile.displayable.getDisplayableDisposition
import io.github.javernaut.mediafile.displayable.toDisplayable

fun VideoStreamFeature.toDisplayableStreamFeature(
    stream: VideoStream,
    resources: Resources
) = when (this) {
    VideoStreamFeature.Codec -> stream.basicInfo.codecName
    VideoStreamFeature.Bitrate -> stream.bitRate.toDisplayable(resources)
    VideoStreamFeature.FrameRate -> stream.frameRate.toDisplayable(resources)
    VideoStreamFeature.FrameWidth -> stream.frameWidth.toString()
    VideoStreamFeature.FrameHeight -> stream.frameHeight.toString()
    VideoStreamFeature.Language -> stream.basicInfo.displayableLanguage
    VideoStreamFeature.Disposition -> stream.basicInfo.getDisplayableDisposition(resources)
}?.let {
    DisplayableStreamFeature(
        name = resources.getString(displayableResource),
        value = it
    )
}

val VideoStreamFeature.displayableResource: Int
    get() = when (this) {
        VideoStreamFeature.Codec -> R.string.page_video_codec_name
        VideoStreamFeature.Bitrate -> R.string.page_video_bit_rate
        VideoStreamFeature.FrameRate -> R.string.page_video_frame_rate
        VideoStreamFeature.FrameWidth -> R.string.page_video_frame_width
        VideoStreamFeature.FrameHeight -> R.string.page_video_frame_height
        VideoStreamFeature.Language -> R.string.page_stream_language
        VideoStreamFeature.Disposition -> R.string.page_stream_disposition
    }
