package com.javernaut.whatthecodec.presentation.subtitle

import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.stream.SimplePage
import com.javernaut.whatthecodec.presentation.stream.StreamFeature
import com.javernaut.whatthecodec.presentation.stream.StreamFeaturesGrid
import io.github.javernaut.mediafile.SubtitleStream
import io.github.javernaut.mediafile.displayable.displayableLanguage
import io.github.javernaut.mediafile.displayable.getDisplayableDisposition

@Composable
fun SubtitlePage(
    streams: List<SubtitleStream>,
    modifier: Modifier = Modifier
) {
    SimplePage(streams, modifier) { item, itemModifier ->
        SubtitleCardContent(item, itemModifier)
    }
}

@Composable
fun SubtitleCardContent(
    stream: SubtitleStream,
    modifier: Modifier = Modifier
) {
    // TODO Filter the list
    // Preference as state?
    val streamFeatures = SubtitleFeature.values().toList()
    if (streamFeatures.isEmpty()) {
        // TODO extract the logic of showing a empty stub into a single place
    } else {
        StreamFeaturesGrid(
            stream,
            streamFeatures,
            modifier
        )
    }
}

enum class SubtitleFeature(
    @StringRes override val key: Int,
    @StringRes override val title: Int
) :
    StreamFeature<SubtitleStream> {

    CODEC(
        key = R.string.settings_content_codec,
        title = R.string.page_subtitle_codec_name
    ) {
        override fun getValue(stream: SubtitleStream, resources: Resources) =
            stream.basicInfo.codecName
    },
    LANGUAGE(
        key = R.string.settings_content_language,
        title = R.string.page_stream_language
    ) {
        override fun getValue(stream: SubtitleStream, resources: Resources) =
            stream.basicInfo.displayableLanguage
    },
    DISPOSITION(
        key = R.string.settings_content_disposition,
        title = R.string.page_stream_disposition
    ) {
        override fun getValue(stream: SubtitleStream, resources: Resources) =
            stream.basicInfo.getDisplayableDisposition(resources)
    };
}
