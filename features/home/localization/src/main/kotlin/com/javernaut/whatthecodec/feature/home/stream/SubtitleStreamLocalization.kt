package com.javernaut.whatthecodec.feature.home.stream

import android.content.res.Resources
import com.javernaut.whatthecodec.feature.home.localization.R
import com.javernaut.whatthecodec.feature.settings.api.content.SubtitleStreamFeature
import io.github.javernaut.mediafile.SubtitleStream
import io.github.javernaut.mediafile.displayable.displayableLanguage
import io.github.javernaut.mediafile.displayable.getDisplayableDisposition

fun SubtitleStreamFeature.toDisplayableStreamFeature(
    stream: SubtitleStream,
    resources: Resources
) = when (this) {
    SubtitleStreamFeature.Codec -> stream.basicInfo.codecName
    SubtitleStreamFeature.Language -> stream.basicInfo.displayableLanguage
    SubtitleStreamFeature.Disposition -> stream.basicInfo.getDisplayableDisposition(resources)
}?.let {
    DisplayableStreamFeature(
        name = resources.getString(displayableResource),
        value = it
    )
}


val SubtitleStreamFeature.displayableResource: Int
    get() = when (this) {
        SubtitleStreamFeature.Codec -> R.string.page_subtitle_codec_name
        SubtitleStreamFeature.Language -> R.string.page_stream_language
        SubtitleStreamFeature.Disposition -> R.string.page_stream_disposition
    }
