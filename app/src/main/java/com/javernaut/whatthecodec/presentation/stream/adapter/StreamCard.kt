package com.javernaut.whatthecodec.presentation.stream.adapter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.compose.common.GridLayout
import com.javernaut.whatthecodec.presentation.stream.model.StreamFeature
import com.javernaut.whatthecodec.presentation.subtitle.ui.TempStreamFeature
import io.github.javernaut.mediafile.MediaStream

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StreamCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable (Modifier) -> Unit
) {
    Card(modifier) {
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
                .padding(
                    horizontal = dimensionResource(
                        id = R.dimen.common_horizontal_spacing
                    )
                ),
            style = MaterialTheme.typography.subtitle1,
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
            tint = Color(0xFF757575),
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

@Composable
fun StreamFeaturesGrid(
    modifier: Modifier = Modifier,
    features: List<StreamFeature>
) {
    GridLayout(modifier, 2) {
        features.forEach {
            StreamFeatureItem(
                stringResource(id = it.title).toUpperCase(),
                it.description
            )
        }
    }
}

@Composable
fun <T : MediaStream> TempStreamFeaturesGrid(
    stream: T,
    features: List<TempStreamFeature<T>>,
    modifier: Modifier = Modifier
) {
    GridLayout(modifier, 2) {
        features.forEach {
            StreamFeatureItem(stream, it)
        }
    }
}
