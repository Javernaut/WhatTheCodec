package com.javernaut.whatthecodec.presentation.stream.adapter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.stream.model.StreamCard
import com.javernaut.whatthecodec.presentation.stream.model.StreamFeature

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun StreamCard(modifier: Modifier = Modifier, streamCard: StreamCard) {
    Surface(modifier, elevation = 1.dp, shape = MaterialTheme.shapes.medium) {
        Column {
            var gridVisible by remember { mutableStateOf(true) }
            StreamCardTopRow(streamCard, gridVisible) {
                gridVisible = !gridVisible
            }
            AnimatedVisibility(visible = gridVisible) {
                StreamFeaturesGrid(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    features = streamCard.features
                )
            }
        }
    }
}

@Composable
private fun StreamCardTopRow(
    streamCard: StreamCard,
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
            text = streamCard.title,
            modifier = Modifier
                .weight(1f)
                .padding(
                    horizontal = dimensionResource(
                        id = R.dimen.common_horizontal_spacing
                    )
                ),
            style = MaterialTheme.typography.subtitle1,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        val angle by animateFloatAsState(targetValue = if (gridVisible) 360f else 180f)
        Image(
            painter = painterResource(id = R.drawable.ic_item_fold),
            contentDescription = null,
            Modifier
                .rotate(angle)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false),
                    onClick = arrowClicked
                )
                .size(dimensionResource(id = R.dimen.common_clickable_item_height)),
            contentScale = ContentScale.Inside
        )
    }
}

@ExperimentalFoundationApi
@Composable
fun StreamFeaturesGrid(
    modifier: Modifier = Modifier,
    features: List<StreamFeature>
) {
    GridLayout(modifier, 2) {
        features.forEach {
            StreamFeatureItem(streamFeature = it)
        }
    }
}

@Composable
fun GridLayout(
    modifier: Modifier = Modifier,
    columns: Int = 1,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        val columnWidth = constraints.maxWidth / columns

        val placables = measurables.map {
            it.measure(Constraints.fixedWidth(columnWidth))
        }

        val chunkedPlacables = placables.chunked(columns)
        val maxHeights = chunkedPlacables.map { it.maxByOrNull { it.height }!!.height }
        val dstHeight = maxHeights.sum()

        var runningY = 0
        layout(constraints.maxWidth, dstHeight) {
            chunkedPlacables.forEachIndexed { index, list ->
                var runningX = 0
                list.forEach {
                    it.placeRelative(runningX, runningY)
                    runningX += columnWidth
                }
                runningY += maxHeights[index]
            }
        }
    }
}
