package com.javernaut.whatthecodec.presentation.audio.ui

import android.content.res.Resources
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.javernaut.mediafile.AudioStream
import com.javernaut.mediafile.displayable.toDisplayable
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.root.viewmodel.MediaFileViewModel
import com.javernaut.whatthecodec.presentation.stream.adapter.StreamCard
import com.javernaut.whatthecodec.presentation.stream.model.StreamCard
import com.javernaut.whatthecodec.presentation.stream.model.StreamFeature
import com.javernaut.whatthecodec.presentation.stream.model.makeStream

private fun convertStream(audioStream: AudioStream, resources: Resources) =
    makeStream(audioStream.basicInfo, resources) {
        add(StreamFeature(R.string.page_audio_codec_name, audioStream.basicInfo.codecName))

        audioStream.bitRate.toDisplayable(resources)?.let {
            add(StreamFeature(R.string.page_audio_bit_rate, it))
        }

        add(StreamFeature(R.string.page_audio_channels, audioStream.channels.toString()))

        audioStream.channelLayout?.let {
            add(StreamFeature(R.string.page_audio_channel_layout, it))
        }
        audioStream.sampleFormat?.let {
            add(StreamFeature(R.string.page_audio_sample_format, it))
        }

        audioStream.sampleRate.toDisplayable(resources)?.let {
            add(StreamFeature(R.string.page_audio_sample_rate, it))
        }
    }


@Composable
fun AudioPage(viewModel: MediaFileViewModel) {
    val audioPageState by viewModel.audioStreamsLiveData.observeAsState()
    audioPageState?.let {
        StreamsPage(streamCards = it.map { convertStream(it, LocalContext.current.resources) })
    }
}

@Composable
fun StreamsPage(streamCards: List<StreamCard>) {
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        streamCardsInColumn(streamCards)
    }
}

fun LazyListScope.streamCardsInColumn(streamCards: List<StreamCard>) {
    val commonModifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
    items(streamCards) {
        StreamCard(commonModifier, it)
    }
}
