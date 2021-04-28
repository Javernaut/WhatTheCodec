package com.javernaut.whatthecodec.presentation.subtitle.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.domain.SubtitleStream
import com.javernaut.whatthecodec.presentation.audio.ui.StreamsPage
import com.javernaut.whatthecodec.presentation.root.viewmodel.MediaFileViewModel
import com.javernaut.whatthecodec.presentation.stream.model.StreamFeature
import com.javernaut.whatthecodec.presentation.stream.model.makeStream

@Composable
private fun convertStream(subtitleStream: SubtitleStream) =
    makeStream(subtitleStream.basicInfo) {
        add(
            StreamFeature(
                R.string.page_subtitle_codec_name,
                subtitleStream.basicInfo.codecName
            )
        )
    }


@Composable
fun SubtitlePage(viewModel: MediaFileViewModel) {
    val subtitlePageState by viewModel.subtitleStreamsLiveData.observeAsState()
    subtitlePageState?.let {
        StreamsPage(streamCards = it.map { convertStream(it) })
    }
}