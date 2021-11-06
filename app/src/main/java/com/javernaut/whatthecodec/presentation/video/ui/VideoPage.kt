package com.javernaut.whatthecodec.presentation.video.ui

import android.app.Activity
import android.content.res.Resources
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.audio.ui.streamCardsInColumn
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.BasicVideoInfo
import com.javernaut.whatthecodec.presentation.stream.model.StreamCard
import com.javernaut.whatthecodec.presentation.stream.model.StreamFeature
import com.javernaut.whatthecodec.presentation.stream.model.makeStream
import com.javernaut.whatthecodec.presentation.video.ui.view.FramesHeader
import com.javernaut.whatthecodec.presentation.video.ui.view.getPreviewViewWidth
import io.github.javernaut.mediafile.displayable.toDisplayable

@Composable
fun VideoPage(videoInfo: BasicVideoInfo) {
    val resources = LocalContext.current.resources
    val videoInfoCards = listOf(
        convertToContainer(videoInfo, resources),
        convertToStream(videoInfo, resources)
    )
    LazyColumn(Modifier.fillMaxSize()) {
        item {
            FramesHeader(videoInfo.preview, getPreviewViewWidth(LocalContext.current as Activity))
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        streamCardsInColumn(videoInfoCards)
    }
}

private fun convertToStream(basicVideoInfo: BasicVideoInfo, resources: Resources): StreamCard {
    val videoStream = basicVideoInfo.videoStream

    return makeStream(videoStream.basicInfo, resources) {
        add(StreamFeature(R.string.page_video_codec_name, videoStream.basicInfo.codecName))

        videoStream.bitRate.toDisplayable(resources)?.let {
            add(StreamFeature(R.string.page_video_bit_rate, it))
        }

        videoStream.frameRate.toDisplayable(resources)?.let {
            add(StreamFeature(R.string.page_video_frame_rate, it))
        }

        add(StreamFeature(R.string.page_video_frame_width, videoStream.frameWidth.toString()))
        add(StreamFeature(R.string.page_video_frame_height, videoStream.frameHeight.toString()))
    }
}

private fun convertToContainer(basicVideoInfo: BasicVideoInfo, resources: Resources): StreamCard {
    return StreamCard(
        resources.getString(R.string.info_container),
        listOf(
            StreamFeature(R.string.info_file_format, basicVideoInfo.fileFormat),

            StreamFeature(
                R.string.info_protocol_title, resources.getString(
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
