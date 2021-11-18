package com.javernaut.whatthecodec.presentation.video.ui

import android.app.Activity
import android.content.res.Resources
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.BasicVideoInfo
import com.javernaut.whatthecodec.presentation.stream.adapter.StreamCard
import com.javernaut.whatthecodec.presentation.stream.adapter.StreamFeaturesGrid
import com.javernaut.whatthecodec.presentation.stream.model.StreamFeature
import com.javernaut.whatthecodec.presentation.stream.model.makeStream
import com.javernaut.whatthecodec.presentation.subtitle.ui.makeCardTitle
import com.javernaut.whatthecodec.presentation.video.ui.view.FramesHeader
import com.javernaut.whatthecodec.presentation.video.ui.view.getPreviewViewWidth
import io.github.javernaut.mediafile.VideoStream
import io.github.javernaut.mediafile.displayable.toDisplayable

@Composable
fun VideoPage(
    videoInfo: BasicVideoInfo,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier) {
        item {
            FramesHeader(videoInfo.preview, getPreviewViewWidth(LocalContext.current as Activity))
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        val commonModifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        item {
            Container(
                basicVideoInfo = videoInfo,
                modifier = commonModifier
            )
        }
        item {
            VideoStream(
                videoStream = videoInfo.videoStream,
                modifier = commonModifier
            )
        }
    }
}

@Composable
private fun Container(
    basicVideoInfo: BasicVideoInfo,
    modifier: Modifier = Modifier
) {
    StreamCard(
        title = stringResource(id = R.string.info_container),
        modifier
    ) {
        StreamFeaturesGrid(it, convertToContainer(basicVideoInfo, LocalContext.current.resources))
    }
}

@Composable
private fun VideoStream(
    videoStream: VideoStream,
    modifier: Modifier = Modifier
) {
    StreamCard(
        title = makeCardTitle(videoStream.basicInfo),
        modifier
    ) {
        StreamFeaturesGrid(it, convertToStream(videoStream, LocalContext.current.resources))
    }
}

private fun convertToContainer(
    basicVideoInfo: BasicVideoInfo,
    resources: Resources
): List<StreamFeature> {
    return listOf(
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
}

private fun convertToStream(
    videoStream: VideoStream,
    resources: Resources
): List<StreamFeature> {

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
