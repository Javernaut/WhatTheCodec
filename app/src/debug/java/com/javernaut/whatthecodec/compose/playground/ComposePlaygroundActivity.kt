package com.javernaut.whatthecodec.compose.playground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javernaut.whatthecodec.compose.theme.WhatTheCodecTheme
import com.javernaut.whatthecodec.feature.settings.data.content.completeEnumSet
import com.javernaut.whatthecodec.home.presentation.MediaFileArgument
import com.javernaut.whatthecodec.home.presentation.MediaFileProvider
import com.javernaut.whatthecodec.home.presentation.MediaType
import com.javernaut.whatthecodec.home.presentation.PreviewLoaderHelper
import com.javernaut.whatthecodec.home.presentation.model.AudioPage
import com.javernaut.whatthecodec.home.presentation.model.ScreenState
import com.javernaut.whatthecodec.home.presentation.model.SubtitlesPage
import com.javernaut.whatthecodec.home.presentation.model.VideoPage
import com.javernaut.whatthecodec.home.ui.screen.EmptyHomeScreen
import com.javernaut.whatthecodec.home.ui.screen.pickAudioFile
import com.javernaut.whatthecodec.home.ui.screen.pickVideoFile
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.javernaut.mediafile.MediaFile
import io.github.javernaut.mediafile.model.MediaInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

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
                        MediaFileArgument(it, MediaType.AUDIO)
                    )
                }

                val onVideoIconClick = pickVideoFile(
                    permissionDenied = viewModel::onPermissionDenied
                ) {
                    viewModel.openMediaFile(
                        MediaFileArgument(it, MediaType.VIDEO)
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

@HiltViewModel
class TestMediaFileViewModel @Inject constructor(
    private val previewLoaderHelper: PreviewLoaderHelper,
    private val mediaFileProvider: MediaFileProvider
) : ViewModel() {
    fun onPermissionDenied() {
        // Whatever, it's test
    }

    private val _screenState = MutableStateFlow<ScreenState?>(null)
    val screenState = _screenState.asStateFlow()

    fun openMediaFile(mediaFileArgument: MediaFileArgument) {
        viewModelScope.launch(Dispatchers.IO) {
            val mediaFileContext = mediaFileProvider.obtainMediaFile(
                mediaFileArgument.uri
            )

            val mediaFile = mediaFileContext?.readMediaInfo()

            withContext(Dispatchers.Main) {
                if (mediaFileContext != null && mediaFile != null) {
                    fileProcessingFlow(mediaFileContext, mediaFile).collect(_screenState)
                }
            }
        }
    }

    private fun fileProcessingFlow(
        mediaFile: MediaFile,
        mediaInfo: MediaInfo
    ): Flow<ScreenState> = flow {
        val audioPage = AudioPage(emptyList(), completeEnumSet())
        val subtitlePage = SubtitlesPage(emptyList(), completeEnumSet())
        videoPagesFlow(mediaFile, mediaInfo).collect {
            emit(
                ScreenState(it, audioPage, subtitlePage)
            )
        }
    }

    private fun videoPagesFlow(
        mediaFile: MediaFile,
        mediaInfo: MediaInfo
    ): Flow<VideoPage?> = flow {
        val videoStream = mediaInfo.videoStream
        if (videoStream == null) {
            emit(null)
        } else {
            previewLoaderHelper.flowFor(mediaFile, mediaInfo).collect {
                emit(
                    VideoPage(
                        it,
                        mediaInfo.container,
                        videoStream,
                        completeEnumSet()
                    )
                )
            }
        }
    }
}
