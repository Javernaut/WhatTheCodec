package com.javernaut.whatthecodec.presentation.compose.common

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun getAppBarElevation() = if (isSystemInDarkTheme()) 1.dp else AppBarDefaults.TopAppBarElevation

@Composable
fun WtcTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    backgroundColor: Color = MaterialTheme.colors.primarySurface,
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: Dp = getAppBarElevation()
) {
    TopAppBar(
        title = title,
        modifier = modifier,
        navigationIcon = navigationIcon,
        actions = {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                actions()
            }
        },
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        elevation = elevation
    )
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