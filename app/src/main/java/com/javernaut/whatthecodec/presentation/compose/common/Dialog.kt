@file:OptIn(ExperimentalMaterial3Api::class)

package com.javernaut.whatthecodec.presentation.compose.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import kotlin.math.max

@Composable
fun WtcDialog(
    title: String,
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    AlertDialog(onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        content = {
            // TODO Hoist the Column to consumers
            Column {
                content()
            }
        },
        title = {
            Text(text = title)
        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    content: @Composable (() -> Unit)? = null,
    shape: Shape = AlertDialogDefaults.shape,
    containerColor: Color = AlertDialogDefaults.containerColor,
    iconContentColor: Color = AlertDialogDefaults.iconContentColor,
    titleContentColor: Color = AlertDialogDefaults.titleContentColor,
    textContentColor: Color = AlertDialogDefaults.textContentColor,
    tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
    properties: DialogProperties = DialogProperties()
) = AlertDialog(onDismissRequest = onDismissRequest, modifier = modifier, properties = properties) {
    AlertDialogContent(
        buttons = {
            AlertDialogFlowRow(
                mainAxisSpacing = ButtonsMainAxisSpacing,
                crossAxisSpacing = ButtonsCrossAxisSpacing
            ) {
                dismissButton?.invoke()
                confirmButton()
            }
        },
        icon = icon,
        title = title,
        content = content,
        shape = shape,
        containerColor = containerColor,
        tonalElevation = tonalElevation,
        buttonContentColor = MaterialTheme.colorScheme.primary,
        iconContentColor = iconContentColor,
        titleContentColor = titleContentColor,
        textContentColor = textContentColor,
    )
}

@Composable
internal fun AlertDialogContent(
    buttons: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)?,
    title: (@Composable () -> Unit)?,
    content: @Composable (() -> Unit)?,
    shape: Shape,
    containerColor: Color,
    tonalElevation: Dp,
    buttonContentColor: Color,
    iconContentColor: Color,
    titleContentColor: Color,
    textContentColor: Color,
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = containerColor,
        tonalElevation = tonalElevation,
    ) {
        Column(
            modifier = Modifier.padding(DialogPadding)
        ) {
            icon?.let {
                CompositionLocalProvider(LocalContentColor provides iconContentColor) {
                    Box(
                        Modifier
                            .padding(DialogHorizontalPadding)
                            .padding(IconPadding)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        icon()
                    }
                }
            }
            title?.let {
                CompositionLocalProvider(LocalContentColor provides titleContentColor) {
                    val textStyle = MaterialTheme.typography.headlineSmall
                    ProvideTextStyle(textStyle) {
                        Box(
                            // Align the title to the center when an icon is present.
                            Modifier
                                .padding(DialogHorizontalPadding)
                                .padding(TitlePadding)
                                .align(
                                    if (icon == null) {
                                        Alignment.Start
                                    } else {
                                        Alignment.CenterHorizontally
                                    }
                                )
                        ) {
                            title()
                        }
                    }
                }
            }
            content?.let {
                CompositionLocalProvider(LocalContentColor provides textContentColor) {
                    val textStyle = MaterialTheme.typography.bodyMedium
                    ProvideTextStyle(textStyle) {
                        Box(
                            Modifier
                                .weight(weight = 1f, fill = false)
                                .padding(TextPadding)
                                .align(Alignment.Start)
                        ) {
                            content()
                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .padding(DialogHorizontalPadding)
                    .align(Alignment.End)
            ) {
                CompositionLocalProvider(LocalContentColor provides buttonContentColor) {
                    val textStyle = MaterialTheme.typography.labelLarge
                    ProvideTextStyle(value = textStyle, content = buttons)
                }
            }
        }
    }
}

// Using vertical paddings here instead of 'all', so the content will not be cut from sides
private val DialogPadding = PaddingValues(vertical = 24.dp)

// Exposing the required padding to consumers
val DialogHorizontalPadding = PaddingValues(horizontal = 24.dp)

private val IconPadding = PaddingValues(bottom = 16.dp)
private val TitlePadding = PaddingValues(bottom = 16.dp)
private val TextPadding = PaddingValues(bottom = 24.dp)

private val ButtonsMainAxisSpacing = 8.dp
private val ButtonsCrossAxisSpacing = 12.dp

/**
 * Simple clone of FlowRow that arranges its children in a horizontal flow with limited
 * customization.
 */
@Composable
internal fun AlertDialogFlowRow(
    mainAxisSpacing: Dp,
    crossAxisSpacing: Dp,
    content: @Composable () -> Unit
) {
    Layout(content) { measurables, constraints ->
        val sequences = mutableListOf<List<Placeable>>()
        val crossAxisSizes = mutableListOf<Int>()
        val crossAxisPositions = mutableListOf<Int>()

        var mainAxisSpace = 0
        var crossAxisSpace = 0

        val currentSequence = mutableListOf<Placeable>()
        var currentMainAxisSize = 0
        var currentCrossAxisSize = 0

        // Return whether the placeable can be added to the current sequence.
        fun canAddToCurrentSequence(placeable: Placeable) =
            currentSequence.isEmpty() || currentMainAxisSize + mainAxisSpacing.roundToPx() +
                    placeable.width <= constraints.maxWidth

        // Store current sequence information and start a new sequence.
        fun startNewSequence() {
            if (sequences.isNotEmpty()) {
                crossAxisSpace += crossAxisSpacing.roundToPx()
            }
            sequences += currentSequence.toList()
            crossAxisSizes += currentCrossAxisSize
            crossAxisPositions += crossAxisSpace

            crossAxisSpace += currentCrossAxisSize
            mainAxisSpace = max(mainAxisSpace, currentMainAxisSize)

            currentSequence.clear()
            currentMainAxisSize = 0
            currentCrossAxisSize = 0
        }

        for (measurable in measurables) {
            // Ask the child for its preferred size.
            val placeable = measurable.measure(constraints)

            // Start a new sequence if there is not enough space.
            if (!canAddToCurrentSequence(placeable)) startNewSequence()

            // Add the child to the current sequence.
            if (currentSequence.isNotEmpty()) {
                currentMainAxisSize += mainAxisSpacing.roundToPx()
            }
            currentSequence.add(placeable)
            currentMainAxisSize += placeable.width
            currentCrossAxisSize = max(currentCrossAxisSize, placeable.height)
        }

        if (currentSequence.isNotEmpty()) startNewSequence()

        val mainAxisLayoutSize = max(mainAxisSpace, constraints.minWidth)

        val crossAxisLayoutSize = max(crossAxisSpace, constraints.minHeight)

        val layoutWidth = mainAxisLayoutSize

        val layoutHeight = crossAxisLayoutSize

        layout(layoutWidth, layoutHeight) {
            sequences.forEachIndexed { i, placeables ->
                val childrenMainAxisSizes = IntArray(placeables.size) { j ->
                    placeables[j].width +
                            if (j < placeables.lastIndex) mainAxisSpacing.roundToPx() else 0
                }
                val arrangement = Arrangement.End
                val mainAxisPositions = IntArray(childrenMainAxisSizes.size) { 0 }
                with(arrangement) {
                    arrange(
                        mainAxisLayoutSize, childrenMainAxisSizes,
                        layoutDirection, mainAxisPositions
                    )
                }
                placeables.forEachIndexed { j, placeable ->
                    placeable.place(
                        x = mainAxisPositions[j],
                        y = crossAxisPositions[i]
                    )
                }
            }
        }
    }
}

