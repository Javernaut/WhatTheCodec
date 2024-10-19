package com.javernaut.whatthecodec.compose.playground

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javernaut.whatthecodec.compose.theme.WhatTheCodecTheme
import com.javernaut.whatthecodec.home.presentation.MediaFileArgument
import com.javernaut.whatthecodec.home.ui.screen.EmptyHomeScreen
import com.javernaut.whatthecodec.home.ui.screen.pickAudioFile
import com.javernaut.whatthecodec.home.ui.screen.pickVideoFile
import dagger.hilt.android.AndroidEntryPoint
import io.github.javernaut.mediafile.creator.MediaType
import io.github.javernaut.mediafile.factory.MediaFileFactory
import io.github.javernaut.mediafile.factory.Request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ComposePlaygroundActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            WhatTheCodecTheme.Dynamic {
                val viewModel = hiltViewModel<TestMediaFileViewModel>()

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

class TestMediaFileViewModel : ViewModel() {
    fun onPermissionDenied() {
        // Whatever, it's test
    }

    fun openMediaFile(mediaFileArgument: MediaFileArgument) {
        viewModelScope.launch(Dispatchers.IO) {
            val mediaFileContext = MediaFileFactory.create(
                Request.Content(mediaFileArgument.uri.toUri()), mediaFileArgument.type
            )

            Log.e("MediaFileFactory", "is mediaFileContext null? ${mediaFileContext == null}")

            mediaFileContext?.dispose()
        }
    }
}
