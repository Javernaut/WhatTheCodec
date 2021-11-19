package com.javernaut.whatthecodec.presentation.subtitle.ui

import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.stream.adapter.StreamCard
import com.javernaut.whatthecodec.presentation.stream.adapter.StreamFeaturesGrid
import io.github.javernaut.mediafile.BasicStreamInfo
import io.github.javernaut.mediafile.MediaStream
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
        itemsIndexed(streams) { _, item: T ->
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

@Composable
fun makeCardTitle(basicStreamInfo: BasicStreamInfo): String {
    val title = basicStreamInfo.title
    val index = basicStreamInfo.index

    val prefix = stringResource(R.string.page_stream_title_prefix)
    return if (title == null) {
        prefix + index
    } else {
        "$prefix$index - $title"
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

interface StreamFeature<T : MediaStream> {
    val key: Int
    val title: Int
    fun getValue(stream: T, resources: Resources): String?
}
