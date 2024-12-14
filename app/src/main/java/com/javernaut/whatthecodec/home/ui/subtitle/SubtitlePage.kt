package com.javernaut.whatthecodec.home.ui.subtitle

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.javernaut.whatthecodec.feature.home.stream.toDisplayableStreamFeature
import com.javernaut.whatthecodec.feature.settings.api.content.SubtitleStreamFeature
import com.javernaut.whatthecodec.home.presentation.model.SubtitlesPage
import com.javernaut.whatthecodec.home.ui.stream.SimplePage
import com.javernaut.whatthecodec.home.ui.stream.StreamFeaturesGrid
import io.github.javernaut.mediafile.model.SubtitleStream

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
