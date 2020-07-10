package com.javernaut.whatthecodec.presentation.audio.ui

import android.os.Bundle
import androidx.lifecycle.Observer
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.domain.AudioStream
import com.javernaut.whatthecodec.presentation.stream.BasePageFragment
import com.javernaut.whatthecodec.presentation.stream.model.StreamFeature
import com.javernaut.whatthecodec.presentation.stream.model.makeStream

class AudioPageFragment : BasePageFragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mediaFileViewModel.audioStreamsLiveData.observe(viewLifecycleOwner, Observer {
            displayStreams(it.map(::convertStream))
        })
    }

    private fun convertStream(audioStream: AudioStream) =
            makeStream(audioStream.basicInfo, resources) {
                add(StreamFeature(R.string.page_audio_codec_name, audioStream.basicInfo.codecName))

                if (audioStream.bitRate > 0) {
                    add(StreamFeature(R.string.page_audio_bit_rate,
                            BitRateHelper.toString(audioStream.bitRate, resources))
                    )
                }
                add(StreamFeature(R.string.page_audio_channels, audioStream.channels.toString()))

                if (audioStream.channelLayout != null) {
                    add(StreamFeature(R.string.page_audio_channel_layout, audioStream.channelLayout))
                }
                if (audioStream.sampleFormat != null) {
                    add(StreamFeature(R.string.page_audio_sample_format, audioStream.sampleFormat))
                }

                add(StreamFeature(R.string.page_audio_sample_rate,
                        SampleRateHelper.toString(audioStream.sampleRate, resources))
                )
            }
}
