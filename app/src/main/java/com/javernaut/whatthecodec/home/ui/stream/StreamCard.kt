package com.javernaut.whatthecodec.home.ui.stream

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.compose.common.GridLayout
import com.javernaut.whatthecodec.compose.theme.WhatTheCodecTheme
import com.javernaut.whatthecodec.feature.home.stream.DisplayableStreamFeature
import io.github.javernaut.mediafile.model.BasicStreamInfo
import io.github.javernaut.mediafile.model.MediaStream

@Composable
fun StreamCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable (Modifier) -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    ) {
        Column {
            var gridVisible by remember { mutableStateOf(true) }
            StreamCardTopRow(title, gridVisible) {
                gridVisible = !gridVisible
            }
            AnimatedVisibility(visible = gridVisible) {
                content(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }
        }
    }
}

@Composable
fun <T : MediaStream> SimplePage(
    streams: List<T>,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    cardContent: @Composable (T, Modifier) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding + PaddingValues(16.dp),
        verticalArrangement = spacedBy(16.dp)
    ) {
        itemsIndexed(streams) { _, item: T ->
            StreamCard(
                title = makeCardTitle(basicStreamInfo = item.basicInfo),
            ) {
                cardContent(item, it)
            }
        }
    }
}

@Composable
fun StreamFeaturesGrid(
    features: List<DisplayableStreamFeature>,
    onCopyValue: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (features.isNotEmpty()) {
        GridLayout(
            modifier = modifier,
            columns = 2
        ) {
            features.forEach {
                StreamFeatureItem(it.name, it.value, onCopyValue, Modifier.fillMaxWidth())
            }
        }
    } else {
        Text(
            text = stringResource(id = R.string.page_stream_no_info),
            textAlign = TextAlign.Center,
            modifier = modifier.padding(vertical = 8.dp)
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

@Composable
private fun StreamCardTopRow(
    title: String,
    gridVisible: Boolean,
    arrowClicked: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(56.dp)
    ) {
        Text(
            text = title,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )

        IconButton(
            onClick = arrowClicked,
            modifier = Modifier.padding(end = 4.dp)
        ) {
            val angle by animateFloatAsState(
                targetValue = if (gridVisible)
                    if (LocalLayoutDirection.current == LayoutDirection.Ltr) 360f else 0f
                else 180f
            )
            Icon(
                imageVector = Icons.Filled.ExpandLess,
                contentDescription = null,
                modifier = Modifier.rotate(angle)
            )
        }
    }
}


@PreviewLightDark
@Composable
private fun StreamCardTopRowPreview() {
    WhatTheCodecTheme.Static {
        StreamCard(
            title = "Title"
        ) {}
    }
}

operator fun PaddingValues.plus(other: PaddingValues) = PaddingValues(
    start = calculateStartPadding(LayoutDirection.Ltr) +
            other.calculateStartPadding(LayoutDirection.Ltr),

    top = calculateTopPadding() + other.calculateTopPadding(),

    end = calculateEndPadding(LayoutDirection.Ltr) +
            other.calculateEndPadding(LayoutDirection.Ltr),

    bottom = calculateBottomPadding() + other.calculateBottomPadding(),
)
