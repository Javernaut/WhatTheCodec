package com.javernaut.whatthecodec.presentation.video.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.audio.ui.BitRateHelper
import com.javernaut.whatthecodec.presentation.audio.ui.StreamCardsInColumn
import com.javernaut.whatthecodec.presentation.root.viewmodel.MediaFileViewModel
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.BasicVideoInfo
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.Preview
import com.javernaut.whatthecodec.presentation.stream.model.StreamCard
import com.javernaut.whatthecodec.presentation.stream.model.StreamFeature
import com.javernaut.whatthecodec.presentation.stream.model.makeStream
import com.javernaut.whatthecodec.presentation.video.ui.view.FramesHeader

@Composable
fun VideoPage(viewModel: MediaFileViewModel) {
    val videoInfo by viewModel.basicVideoInfoLiveData.observeAsState()

    val preview by viewModel.previewLiveData.observeAsState()
    preview?.let {
        VideoPage(it, videoInfo)
    }
}

@Composable
private fun VideoPage(preview: Preview, videoInfo: BasicVideoInfo?) {
    val videoInfoCards = videoInfo?.let {
        listOf(
            convertToContainer(it),
            convertToStream(it)
        )
    }
    LazyColumn(Modifier.fillMaxSize()) {
        item {
            // TODO fix it
//                FramesHeader(preview, getPreviewViewWidth(requireActivity()))
            FramesHeader(preview, 100)
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        videoInfoCards?.let {
            StreamCardsInColumn(videoInfoCards)
        }
    }
}

@Composable
private fun convertToStream(basicVideoInfo: BasicVideoInfo): StreamCard {
    val videoStream = basicVideoInfo.videoStream

    return makeStream(videoStream.basicInfo) {
        add(StreamFeature(R.string.page_video_codec_name, videoStream.basicInfo.codecName))
        if (videoStream.bitRate > 0) {
            add(
                StreamFeature(
                    R.string.page_video_bit_rate,
                    BitRateHelper.toString(videoStream.bitRate)
                )
            )
        }

        add(StreamFeature(R.string.page_video_frame_width, videoStream.frameWidth.toString()))
        add(StreamFeature(R.string.page_video_frame_height, videoStream.frameHeight.toString()))
    }
}

@Composable
private fun convertToContainer(basicVideoInfo: BasicVideoInfo): StreamCard {
    return StreamCard(
        stringResource(R.string.info_container),
        listOf(
            StreamFeature(R.string.info_file_format, basicVideoInfo.fileFormat),

            StreamFeature(
                R.string.info_protocol_title, stringResource(
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