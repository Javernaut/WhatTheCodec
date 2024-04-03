package com.javernaut.whatthecodec.home.ui.video

import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.home.presentation.model.BasicVideoInfo
import com.javernaut.whatthecodec.home.ui.stream.StreamCard
import com.javernaut.whatthecodec.home.ui.stream.StreamFeature
import com.javernaut.whatthecodec.home.ui.stream.StreamFeatureItem
import com.javernaut.whatthecodec.home.ui.stream.StreamFeaturesGrid
import com.javernaut.whatthecodec.home.ui.stream.getFilteredStreamFeatures
import com.javernaut.whatthecodec.home.ui.stream.makeCardTitle
import com.javernaut.whatthecodec.settings.PreferencesKeys
import io.github.javernaut.mediafile.VideoStream
import io.github.javernaut.mediafile.displayable.displayableLanguage
import io.github.javernaut.mediafile.displayable.getDisplayableDisposition
import io.github.javernaut.mediafile.displayable.toDisplayable

@Composable
fun VideoPage(
    videoInfo: BasicVideoInfo,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        item {
            FramesHeader(videoInfo.preview, Modifier.fillMaxWidth())
        }
        val commonModifier = Modifier.padding(horizontal = 16.dp)
        item {
            Container(
                basicVideoInfo = videoInfo,
                modifier = commonModifier
                    .padding(top = 16.dp)
            )
        }
        item {
            VideoStream(
                videoStream = videoInfo.videoStream,
                modifier = commonModifier.padding(vertical = 16.dp)
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
        Row(modifier = it) {
            StreamFeatureItem(
                title = stringResource(id = R.string.info_file_format),
                value = basicVideoInfo.fileFormat,
                modifier = Modifier.weight(1f)
            )
            StreamFeatureItem(
                title = stringResource(id = R.string.info_protocol_title),
                value = stringResource(
                    id = if (basicVideoInfo.fullFeatured) {
                        R.string.info_protocol_file
                    } else {
                        R.string.info_protocol_pipe
                    }
                ),
                modifier = Modifier.weight(1f)
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
        val streamFeatures = getFilteredStreamFeatures(
            defaultValueResId = R.array.settings_content_video_entryValues,
            preferenceKey = PreferencesKeys.VIDEO,
            allValues = VideoFeature.entries
        )

        StreamFeaturesGrid(
            stream = videoStream,
            features = streamFeatures,
            modifier = it
        )
    }
}

private enum class VideoFeature(
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
