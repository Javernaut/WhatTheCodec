package com.javernaut.whatthecodec.home.ui.audio

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.javernaut.whatthecodec.feature.home.stream.toDisplayableStreamFeature
import com.javernaut.whatthecodec.feature.settings.api.content.AudioStreamFeature
import com.javernaut.whatthecodec.home.presentation.model.AudioPage
import com.javernaut.whatthecodec.home.ui.stream.SimplePage
import com.javernaut.whatthecodec.home.ui.stream.StreamFeaturesGrid
import io.github.javernaut.mediafile.AudioStream

@Composable
fun AudioPage(
    audioPage: AudioPage,
    contentPadding: PaddingValues,
    onCopyValue: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    SimplePage(audioPage.streams, contentPadding, modifier) { item, itemModifier ->
        AudioCardContent(item, audioPage.streamFeatures, onCopyValue, itemModifier)
    }
}

@Composable
private fun AudioCardContent(
    stream: AudioStream,
    streamFeatures: Set<AudioStreamFeature>,
    onCopyValue: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val resources = LocalContext.current.resources
    val displayableStreamFeatures =
        AudioStreamFeature.entries.filter {
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
