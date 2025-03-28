package com.javernaut.whatthecodec.home.presentation.model

import com.javernaut.whatthecodec.feature.settings.api.content.AudioStreamFeature
import com.javernaut.whatthecodec.feature.settings.api.content.SubtitleStreamFeature
import com.javernaut.whatthecodec.feature.settings.api.content.VideoStreamFeature
import io.github.javernaut.mediafile.model.AudioStream
import io.github.javernaut.mediafile.model.Container
import io.github.javernaut.mediafile.model.SubtitleStream
import io.github.javernaut.mediafile.model.VideoStream

data class VideoPage(
    val preview: Preview,
    val container: Container,
    val videoStream: VideoStream,
    val videoStreamFeatures: Set<VideoStreamFeature>
)

data class AudioPage(
    val streams: List<AudioStream>,
    val streamFeatures: Set<AudioStreamFeature>
)

data class SubtitlesPage(
    val streams: List<SubtitleStream>,
    val streamFeatures: Set<SubtitleStreamFeature>
)
