package com.javernaut.whatthecodec.home.ui.video

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.feature.home.stream.toDisplayableStreamFeature
import com.javernaut.whatthecodec.feature.settings.api.content.VideoStreamFeature
import com.javernaut.whatthecodec.home.presentation.model.VideoPage
import com.javernaut.whatthecodec.home.ui.stream.StreamCard
import com.javernaut.whatthecodec.home.ui.stream.StreamFeatureItem
import com.javernaut.whatthecodec.home.ui.stream.StreamFeaturesGrid
import com.javernaut.whatthecodec.home.ui.stream.makeCardTitle
import com.javernaut.whatthecodec.home.ui.stream.plus
import io.github.javernaut.mediafile.model.VideoStream

@Composable
fun VideoPage(
    videoPage: VideoPage,
    contentPadding: PaddingValues,
    onCopyValue: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = spacedBy(16.dp),
        contentPadding = contentPadding + PaddingValues(16.dp)
    ) {
        item {
            FramesHeader(
                videoPage.preview,
            )
        }
        item {
            Container(
                fileFormat = videoPage.container.formatName,
                protocol = stringResource(id = R.string.info_protocol_content),
                onCopyValue = onCopyValue,
            )
        }
        item {
            VideoStream(
                stream = videoPage.videoStream,
                streamFeatures = videoPage.videoStreamFeatures,
                onCopyValue = onCopyValue,
            )
        }
    }
}

@Composable
private fun Container(
    fileFormat: String,
    protocol: String,
    onCopyValue: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    StreamCard(
        title = stringResource(id = R.string.info_container),
        modifier = modifier
    ) {
        Row(modifier = it) {
            StreamFeatureItem(
                title = stringResource(id = R.string.info_file_format),
                value = fileFormat,
                onCopyValue = onCopyValue,
                modifier = Modifier.weight(1f)
            )
            StreamFeatureItem(
                title = stringResource(id = R.string.info_protocol_title),
                value = protocol,
                onCopyValue = onCopyValue,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun VideoStream(
    stream: VideoStream,
    streamFeatures: Set<VideoStreamFeature>,
    onCopyValue: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    StreamCard(
        title = makeCardTitle(stream.basicInfo),
        modifier = modifier
    ) {
        val resources = LocalResources.current
        val displayableStreamFeatures =
            VideoStreamFeature.entries.filter {
                streamFeatures.contains(it)
            }.mapNotNull {
                it.toDisplayableStreamFeature(stream, resources)
            }

        StreamFeaturesGrid(
            features = displayableStreamFeatures,
            onCopyValue = onCopyValue,
            modifier = it
        )
    }
}
