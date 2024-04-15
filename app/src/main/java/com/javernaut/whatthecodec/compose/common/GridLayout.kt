package com.javernaut.whatthecodec.compose.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.nextUp
import kotlin.math.roundToInt

@Composable
fun GridLayout(
    modifier: Modifier = Modifier,
    columns: Int = 1,
    horizontalSpacing: Dp = 0.dp,
    verticalSpacing: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        val rows = (measurables.size / columns.toFloat()).nextUp().roundToInt()

        val horizontalSpacingPx = horizontalSpacing.roundToPx()
        val verticalSpacingPx = verticalSpacing.roundToPx()

        val totalHorizontalSpacing = horizontalSpacingPx * (columns - 1)
        val totalVerticalSpacing = verticalSpacingPx * (rows - 1)

        val columnWidth = (constraints.maxWidth - totalHorizontalSpacing) / columns

        val placables = measurables.map {
            it.measure(Constraints(maxWidth = columnWidth))
        }

        val chunkedPlacables = placables.chunked(columns)
        val maxHeights = chunkedPlacables.map { it.maxByOrNull { it.height }!!.height }
        val maxWidth = chunkedPlacables.maxOf { it.sumOf { it.width } }
        val dstHeight = maxHeights.sum() + totalVerticalSpacing
        val dstWidth = maxWidth + totalHorizontalSpacing

        var runningY = 0
        layout(dstWidth, dstHeight) {
            chunkedPlacables.forEachIndexed { index, list ->
                var runningX = 0
                list.forEach {
                    it.placeRelative(runningX, runningY)
                    // TODO The grid isn't actually aligned,
                    //  since we advance by individual width, and not by the mas in a column
                    runningX += it.width + horizontalSpacingPx
                }
                runningY += maxHeights[index] + verticalSpacingPx
            }
        }
    }
}
