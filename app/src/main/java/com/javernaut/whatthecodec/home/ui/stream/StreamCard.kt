package com.javernaut.whatthecodec.home.ui.stream

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.compose.common.GridLayout
import io.github.javernaut.mediafile.BasicStreamInfo
import io.github.javernaut.mediafile.MediaStream

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
fun <T : MediaStream> StreamFeaturesGrid(
    stream: T,
    features: List<StreamFeature<T>>,
    onCopyValue: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val resources = LocalContext.current.resources
    val displayableFeatures = features.filter {
        it.getValue(stream, resources) != null
    }

    if (displayableFeatures.isNotEmpty()) {
        GridLayout(
            modifier = modifier,
            columns = 2
        ) {
            displayableFeatures.forEach {
                StreamFeatureItem(stream, it, onCopyValue, Modifier.fillMaxWidth())
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
        Modifier
            .height(56.dp)
            .padding(end = 5.dp),
        verticalAlignment = Alignment.CenterVertically
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

        val angle by animateFloatAsState(
            targetValue = if (gridVisible)
                if (LocalLayoutDirection.current == LayoutDirection.Ltr) 360f else 0f
            else 180f
        )
        Icon(
            Icons.Filled.ExpandLess,
            contentDescription = null,
            modifier = Modifier
                .rotate(angle)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false),
                    onClick = arrowClicked
                )
                .padding(12.dp),
        )
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
