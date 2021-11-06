package com.javernaut.whatthecodec.presentation.subtitle.ui

import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.audio.ui.StreamsPage
import com.javernaut.whatthecodec.presentation.stream.model.StreamFeature
import com.javernaut.whatthecodec.presentation.stream.model.makeStream
import io.github.javernaut.mediafile.SubtitleStream

private fun convertStream(subtitleStream: SubtitleStream, resources: Resources) =
    makeStream(subtitleStream.basicInfo, resources) {
        add(
            StreamFeature(
                R.string.page_subtitle_codec_name,
                subtitleStream.basicInfo.codecName
            )
        )
    }


@Composable
fun SubtitlePage(streams: List<SubtitleStream>) {
    StreamsPage(streamCards = streams.map { convertStream(it, LocalContext.current.resources) })
}
