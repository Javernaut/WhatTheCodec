package com.javernaut.whatthecodec.home.ui.subtitle

import android.content.res.Resources
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.feature.settings.content.SubtitleStreamFeature
import com.javernaut.whatthecodec.home.presentation.model.SubtitlesPage
import com.javernaut.whatthecodec.home.ui.stream.DisplayableStreamFeature
import com.javernaut.whatthecodec.home.ui.stream.SimplePage
import com.javernaut.whatthecodec.home.ui.stream.StreamFeaturesGrid
import io.github.javernaut.mediafile.SubtitleStream
import io.github.javernaut.mediafile.displayable.displayableLanguage
import io.github.javernaut.mediafile.displayable.getDisplayableDisposition

@Composable
fun SubtitlePage(
    subtitlesPage: SubtitlesPage,
    contentPadding: PaddingValues,
    onCopyValue: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    SimplePage(subtitlesPage.streams, contentPadding, modifier) { item, itemModifier ->
        SubtitleCardContent(item, subtitlesPage.streamFeatures, onCopyValue, itemModifier)
    }
}

@Composable
private fun SubtitleCardContent(
    stream: SubtitleStream,
    streamFeatures: Set<SubtitleStreamFeature>,
    onCopyValue: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val resources = LocalContext.current.resources
    val displayableStreamFeatures =
        SubtitleStreamFeature.entries.filter {
            streamFeatures.contains(it)
        }.mapNotNull {
            it.toDisplayableStreamFeature(stream, resources)
        }

    StreamFeaturesGrid(
        displayableStreamFeatures,
        onCopyValue,
        modifier
    )
}

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
