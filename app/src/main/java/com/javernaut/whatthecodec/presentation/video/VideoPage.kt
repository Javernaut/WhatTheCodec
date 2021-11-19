package com.javernaut.whatthecodec.presentation.video

import android.app.Activity
import android.content.res.Resources
import androidx.annotation.StringRes
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
import com.javernaut.whatthecodec.presentation.compose.common.GridLayout
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.BasicVideoInfo
import com.javernaut.whatthecodec.presentation.stream.StreamCard
import com.javernaut.whatthecodec.presentation.stream.StreamFeature
import com.javernaut.whatthecodec.presentation.stream.StreamFeatureItem
import com.javernaut.whatthecodec.presentation.stream.StreamFeaturesGrid
import com.javernaut.whatthecodec.presentation.stream.makeCardTitle
import com.javernaut.whatthecodec.presentation.video.ui.view.FramesHeader
import com.javernaut.whatthecodec.presentation.video.ui.view.getPreviewViewWidth
import io.github.javernaut.mediafile.VideoStream
import io.github.javernaut.mediafile.displayable.displayableLanguage
import io.github.javernaut.mediafile.displayable.getDisplayableDisposition
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
        modifier = modifier
    ) {
        GridLayout(modifier = it, columns = 2) {
            StreamFeatureItem(
                title = stringResource(id = R.string.info_file_format).toUpperCase(),
                value = basicVideoInfo.fileFormat
            )
            StreamFeatureItem(
                title = stringResource(id = R.string.info_protocol_title).toUpperCase(),
                value = stringResource(
                    id = if (basicVideoInfo.fullFeatured) {
                        R.string.info_protocol_file
                    } else {
                        R.string.info_protocol_pipe
                    }
                )
            )
        }
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
        // TODO Filter it
        val streamFeatures = VideoFeature.values().toList()
        StreamFeaturesGrid(stream = videoStream, features = streamFeatures, modifier = it)
    }
}

enum class VideoFeature(
    @StringRes override val key: Int,
    @StringRes override val title: Int
) :
    StreamFeature<VideoStream> {

    CODEC(
        key = R.string.settings_content_codec,
        title = R.string.page_video_codec_name
    ) {
        override fun getValue(stream: VideoStream, resources: Resources) =
            stream.basicInfo.codecName
    },
    BITRATE(
        key = R.string.settings_content_bitrate,
        title = R.string.page_video_bit_rate
    ) {
        override fun getValue(stream: VideoStream, resources: Resources) =
            stream.bitRate.toDisplayable(resources)
    },
    FRAME_RATE(
        key = R.string.settings_content_video_frame_rate,
        title = R.string.page_video_frame_rate
    ) {
        override fun getValue(stream: VideoStream, resources: Resources) =
            stream.frameRate.toDisplayable(resources)
    },
    FRAME_WIDTH(
        key = R.string.settings_content_video_width,
        title = R.string.page_video_frame_width
    ) {
        override fun getValue(stream: VideoStream, resources: Resources) =
            stream.frameWidth.toString()
    },
    FRAME_HEIGHT(
        key = R.string.settings_content_video_height,
        title = R.string.page_video_frame_height
    ) {
        override fun getValue(stream: VideoStream, resources: Resources) =
            stream.frameHeight.toString()
    },
    LANGUAGE(
        key = R.string.settings_content_language,
        title = R.string.page_stream_language
    ) {
        override fun getValue(stream: VideoStream, resources: Resources) =
            stream.basicInfo.displayableLanguage
    },
    DISPOSITION(
        key = R.string.settings_content_disposition,
        title = R.string.page_stream_disposition
    ) {
        override fun getValue(stream: VideoStream, resources: Resources) =
            stream.basicInfo.getDisplayableDisposition(resources)
    };
}
