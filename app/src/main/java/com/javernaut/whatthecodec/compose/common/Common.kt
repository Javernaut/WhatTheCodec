package com.javernaut.whatthecodec.compose.common

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun OrientationLayout(
    portraitContent: @Composable () -> Unit,
    landscapeContent: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier) {
        if (maxWidth < maxHeight) {
            portraitContent()
        } else {
            landscapeContent()
        }
    }
}
