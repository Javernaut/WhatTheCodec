package com.javernaut.whatthecodec.presentation.subtitle

import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.settings.PreferencesKeys
import com.javernaut.whatthecodec.presentation.stream.SimplePage
import com.javernaut.whatthecodec.presentation.stream.StreamFeature
import com.javernaut.whatthecodec.presentation.stream.StreamFeaturesGrid
import com.javernaut.whatthecodec.presentation.stream.getFilteredStreamFeatures
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
private fun SubtitleCardContent(
    stream: SubtitleStream,
    modifier: Modifier = Modifier
) {
    val streamFeatures = getFilteredStreamFeatures(
        defaultValueResId = R.array.settings_content_subtitles_entryValues,
        preferenceKey = PreferencesKeys.SUBTITLES,
        allValues = SubtitleFeature.values().toList()
    )

    StreamFeaturesGrid(
        stream,
        streamFeatures,
        modifier
    )
}

private enum class SubtitleFeature(
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
