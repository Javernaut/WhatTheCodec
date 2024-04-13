package com.javernaut.whatthecodec.home.ui.subtitle

import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.home.ui.stream.SimplePage
import com.javernaut.whatthecodec.home.ui.stream.StreamFeature
import com.javernaut.whatthecodec.home.ui.stream.StreamFeaturesGrid
import com.javernaut.whatthecodec.home.ui.stream.getFilteredStreamFeatures
import com.javernaut.whatthecodec.settings.PreferencesKeys
import io.github.javernaut.mediafile.SubtitleStream
import io.github.javernaut.mediafile.displayable.displayableLanguage
import io.github.javernaut.mediafile.displayable.getDisplayableDisposition

@Composable
fun SubtitlePage(
    streams: List<SubtitleStream>,
    contentPadding: PaddingValues,
    onCopyValue: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    SimplePage(streams, contentPadding, modifier) { item, itemModifier ->
        SubtitleCardContent(item, onCopyValue, itemModifier)
    }
}

@Composable
private fun SubtitleCardContent(
    stream: SubtitleStream,
    onCopyValue: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val streamFeatures = getFilteredStreamFeatures(
        defaultValueResId = R.array.settings_content_subtitles_entryValues,
        preferenceKey = PreferencesKeys.SUBTITLES,
        allValues = SubtitleFeature.entries
    )

    StreamFeaturesGrid(
        stream,
        streamFeatures,
        onCopyValue,
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
