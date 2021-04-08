package com.javernaut.whatthecodec.presentation.video.ui

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.audio.ui.BitRateHelper
import com.javernaut.whatthecodec.presentation.compose.theme.WhatTheCodecTheme
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.BasicVideoInfo
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.Preview
import com.javernaut.whatthecodec.presentation.stream.BasePageFragment
import com.javernaut.whatthecodec.presentation.stream.model.StreamCard
import com.javernaut.whatthecodec.presentation.stream.model.StreamFeature
import com.javernaut.whatthecodec.presentation.stream.model.makeStream
import com.javernaut.whatthecodec.presentation.video.ui.view.FramesHeader
import com.javernaut.whatthecodec.presentation.video.ui.view.getPreviewViewWidth

class VideoPageFragment : BasePageFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        composeView.setContent {
            val videoInfo by mediaFileViewModel.basicVideoInfoLiveData.observeAsState()

            val preview by mediaFileViewModel.previewLiveData.observeAsState()
            preview?.let {
                VideoPage(it, videoInfo)
            }
        }
    }

    @Composable
    private fun VideoPage(preview: Preview, videoInfo: BasicVideoInfo?) {
        WhatTheCodecTheme {
            LazyColumn(Modifier.fillMaxSize()) {
                item {
                    FramesHeader(preview, getPreviewViewWidth(requireActivity()))
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
                videoInfo?.let {
                    StreamCardsInColumn(
                        listOf(
                            convertToContainer(it),
                            convertToStream(it)
                        )
                    )
                }
            }
        }
    }

    private fun convertToStream(basicVideoInfo: BasicVideoInfo): StreamCard {
        val videoStream = basicVideoInfo.videoStream

        return makeStream(videoStream.basicInfo, resources) {
            add(StreamFeature(R.string.page_video_codec_name, videoStream.basicInfo.codecName))
            if (videoStream.bitRate > 0) {
                add(
                    StreamFeature(
                        R.string.page_video_bit_rate,
                        BitRateHelper.toString(videoStream.bitRate, resources)
                    )
                )
            }

            add(StreamFeature(R.string.page_video_frame_width, videoStream.frameWidth.toString()))
            add(StreamFeature(R.string.page_video_frame_height, videoStream.frameHeight.toString()))
        }
    }

    private fun convertToContainer(basicVideoInfo: BasicVideoInfo): StreamCard {
        return StreamCard(
            getString(R.string.info_container),
            listOf(
                StreamFeature(R.string.info_file_format, basicVideoInfo.fileFormat),

                StreamFeature(
                    R.string.info_protocol_title, getString(
                        if (basicVideoInfo.fullFeatured) {
                            R.string.info_protocol_file
                        } else {
                            R.string.info_protocol_pipe
                        }
                    )
                )
            )
        )
    }
}
