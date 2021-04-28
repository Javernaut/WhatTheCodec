package com.javernaut.whatthecodec.presentation.audio.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.domain.AudioStream
import com.javernaut.whatthecodec.presentation.root.viewmodel.MediaFileViewModel
import com.javernaut.whatthecodec.presentation.stream.adapter.StreamCard
import com.javernaut.whatthecodec.presentation.stream.model.StreamCard
import com.javernaut.whatthecodec.presentation.stream.model.StreamFeature
import com.javernaut.whatthecodec.presentation.stream.model.makeStream

@Composable
private fun convertStream(audioStream: AudioStream) =
    makeStream(audioStream.basicInfo) {
        add(StreamFeature(R.string.page_audio_codec_name, audioStream.basicInfo.codecName))

        if (audioStream.bitRate > 0) {
            add(
                StreamFeature(
                    R.string.page_audio_bit_rate,
                    BitRateHelper.toString(audioStream.bitRate)
                )
            )
        }
        add(StreamFeature(R.string.page_audio_channels, audioStream.channels.toString()))

        if (audioStream.channelLayout != null) {
            add(StreamFeature(R.string.page_audio_channel_layout, audioStream.channelLayout))
        }
        if (audioStream.sampleFormat != null) {
            add(StreamFeature(R.string.page_audio_sample_format, audioStream.sampleFormat))
        }

        add(
            StreamFeature(
                R.string.page_audio_sample_rate,
                SampleRateHelper.toString(audioStream.sampleRate)
            )
        )
    }


@Composable
fun AudioPage(viewModel: MediaFileViewModel) {
    val audioPageState by viewModel.audioStreamsLiveData.observeAsState()
    audioPageState?.let {
        StreamsPage(streamCards = it.map { convertStream(it) })
    }
}

@Composable
fun StreamsPage(streamCards: List<StreamCard>) {
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        StreamCardsInColumn(streamCards)
    }
}

fun LazyListScope.StreamCardsInColumn(streamCards: List<StreamCard>) {
    val commonModifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
    items(streamCards) {
        StreamCard(commonModifier, it)
    }
}