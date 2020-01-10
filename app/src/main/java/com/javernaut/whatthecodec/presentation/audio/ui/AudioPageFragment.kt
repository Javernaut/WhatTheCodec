package com.javernaut.whatthecodec.presentation.audio.ui

import android.os.Bundle
import androidx.lifecycle.Observer
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.domain.AudioStream
import com.javernaut.whatthecodec.presentation.stream.BasePageFragment
import com.javernaut.whatthecodec.presentation.stream.helper.DispositionHelper
import com.javernaut.whatthecodec.presentation.stream.helper.LanguageHelper
import com.javernaut.whatthecodec.presentation.stream.model.Stream
import com.javernaut.whatthecodec.presentation.stream.model.StreamFeature

class AudioPageFragment : BasePageFragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mediaFileViewModel.audioStreamsLiveData.observe(this, Observer {
            displayStreams(it.map { audioStream ->
                Stream(audioStream.index, audioStream.title, getFeaturesList(audioStream))
            })
        })
    }

    private fun getFeaturesList(stream: AudioStream): List<StreamFeature> =
            mutableListOf<StreamFeature>().apply {
                add(StreamFeature(R.string.page_audio_codec_name, stream.codecName))
                add(StreamFeature(R.string.page_audio_bit_rate, BitRateHelper.toString(stream.bitRate, resources)))
                add(StreamFeature(R.string.page_audio_channels, stream.channels.toString()))

                if (stream.channelLayout != null) {
                    add(StreamFeature(R.string.page_audio_channel_layout, stream.channelLayout))
                }
                if (stream.sampleFormat != null) {
                    add(StreamFeature(R.string.page_audio_sample_format, stream.sampleFormat))
                }

                add(StreamFeature(R.string.page_audio_sample_rate, SampleRateHelper.toString(stream.sampleRate, resources)))

                val language = LanguageHelper.getDisplayName(stream.language)
                if (language != null) {
                    add(StreamFeature(R.string.page_stream_language, language))
                }

                if (DispositionHelper.isDisplayable(stream.disposition)) {
                    add(StreamFeature(
                            R.string.page_stream_disposition,
                            DispositionHelper.toString(stream.disposition, resources))
                    )
                }
            }
}