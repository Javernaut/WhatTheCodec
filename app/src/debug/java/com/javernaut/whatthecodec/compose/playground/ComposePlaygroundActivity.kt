package com.javernaut.whatthecodec.compose.playground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.navigation.compose.hiltViewModel
import com.javernaut.whatthecodec.compose.theme.WhatTheCodecTheme
import com.javernaut.whatthecodec.home.presentation.MediaFileArgument
import com.javernaut.whatthecodec.home.presentation.MediaFileViewModel
import com.javernaut.whatthecodec.home.ui.screen.EmptyHomeScreen
import com.javernaut.whatthecodec.home.ui.screen.pickAudioFile
import com.javernaut.whatthecodec.home.ui.screen.pickVideoFile
import dagger.hilt.android.AndroidEntryPoint
import io.github.javernaut.mediafile.creator.MediaType
import kotlinx.coroutines.flow.emptyFlow

@AndroidEntryPoint
class ComposePlaygroundActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            WhatTheCodecTheme.Dynamic {
                val viewModel = hiltViewModel<MediaFileViewModel>()

                val onAudioIconClick = pickAudioFile(
                    permissionDenied = viewModel::onPermissionDenied
                ) {
                    viewModel.openMediaFile(
                        MediaFileArgument(
                            it.toString(),
                            MediaType.AUDIO
                        )
                    )
                }

                val onVideoIconClick = pickVideoFile(
                    permissionDenied = viewModel::onPermissionDenied
                ) {
                    viewModel.openMediaFile(
                        MediaFileArgument(
                            it.toString(),
                            MediaType.VIDEO
                        )
                    )
                }

                EmptyHomeScreen(
                    onVideoIconClick = onVideoIconClick,
                    onAudioIconClick = onAudioIconClick,
                    onSettingsClicked = {

                    }, emptyFlow()
                )
            }
        }
    }
}
