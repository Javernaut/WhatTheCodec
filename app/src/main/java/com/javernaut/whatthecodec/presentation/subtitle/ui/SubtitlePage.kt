package com.javernaut.whatthecodec.presentation.subtitle.ui

import android.content.res.Resources
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.stream.adapter.StreamCard
import com.javernaut.whatthecodec.presentation.stream.adapter.StreamFeaturesGrid
import com.javernaut.whatthecodec.presentation.stream.model.StreamFeature
import com.javernaut.whatthecodec.presentation.stream.model.makeStream
import io.github.javernaut.mediafile.BasicStreamInfo
import io.github.javernaut.mediafile.MediaStream
import io.github.javernaut.mediafile.SubtitleStream

private fun convertStream(subtitleStream: SubtitleStream, resources: Resources) =
    makeStream(subtitleStream.basicInfo, resources) {
        add(
            StreamFeature(
                R.string.page_subtitle_codec_name,
                subtitleStream.basicInfo.codecName
            )
        )
    }

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
fun <T : MediaStream> SimplePage(
    streams: List<T>,
    modifier: Modifier = Modifier,
    cardContent: @Composable (T, Modifier) -> Unit
) {
    val commonPaddingValues = PaddingValues(8.dp)
    val commonItemModifier = Modifier.padding(commonPaddingValues)
    LazyColumn(
        modifier = modifier,
        contentPadding = commonPaddingValues
    ) {
        itemsIndexed(streams) { index: Int, item: T ->
            StreamCard(
                title = makeCardTitle(basicStreamInfo = item.basicInfo),
                modifier = commonItemModifier,
            ) {
                cardContent(item, it)
            }
        }
    }
}

@Composable
fun SubtitleCardContent(
    stream: SubtitleStream,
    modifier: Modifier = Modifier
) {
    val convertedStream = convertStream(stream, LocalContext.current.resources)
    StreamFeaturesGrid(
        features = convertedStream.features,
        modifier = modifier
    )
}

@Composable
private fun makeCardTitle(basicStreamInfo: BasicStreamInfo): String {
    val title = basicStreamInfo.title
    val index = basicStreamInfo.index

    val prefix = stringResource(R.string.page_stream_title_prefix)
    return if (title == null) {
        prefix + index
    } else {
        "$prefix$index - $title"
    }
}
