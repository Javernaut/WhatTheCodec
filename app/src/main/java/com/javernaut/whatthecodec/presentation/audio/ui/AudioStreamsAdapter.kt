package com.javernaut.whatthecodec.presentation.audio.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.domain.AudioStream
import com.javernaut.whatthecodec.util.setVisible
import com.javernaut.whatthecodec.util.setupTwoLineView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_audio_stream.view.*

class AudioStreamsAdapter : RecyclerView.Adapter<AudioStreamViewHolder>() {

    var streams = emptyList<AudioStream>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioStreamViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_audio_stream, parent, false)
        return AudioStreamViewHolder(itemView)
    }

    override fun getItemCount() = streams.size

    override fun onBindViewHolder(holder: AudioStreamViewHolder, position: Int) {
        holder.bindTo(streams[position])
    }

}

class AudioStreamViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bindTo(stream: AudioStream) {
        containerView.streamIndex.setupTwoLineView(R.string.page_audio_stream_index, stream.index.toString())

        containerView.streamTitle.applyText(R.string.page_audio_stream_title, stream.title)

        containerView.codecName.setupTwoLineView(R.string.page_audio_codec_name, stream.codecName)

        containerView.streamBitRate.setupTwoLineView(R.string.page_audio_bit_rate, BitRateHelper.toString(stream.bitRate, containerView.resources))

        containerView.channels.setupTwoLineView(R.string.page_audio_channels, stream.channels.toString())

        containerView.channelLayout.applyText(R.string.page_audio_channel_layout, stream.channelLayout)

        containerView.sampleFormat.applyText(R.string.page_audio_sample_format, stream.sampleFormat)

        containerView.sampleRate.setupTwoLineView(R.string.page_audio_sample_rate, SampleRateHelper.toString(stream.sampleRate, containerView.resources))

        containerView.streamLanguage.applyText(R.string.page_audio_stream_language, stream.language?.capitalize())

        containerView.streamDisposition.setVisible(!DispositionHelper.isEmpty(stream.disposition))
        if (!DispositionHelper.isEmpty(stream.disposition)) {
            containerView.streamDisposition.setupTwoLineView(
                    R.string.page_audio_stream_disposition,
                    DispositionHelper.toString(stream.disposition, containerView.resources)
            )
        }

        // TODO check where are sample format and channel layout
    }

    private fun View.applyText(text1: Int, text2: String?) {
        setVisible(text2 != null)
        if (text2 != null) {
            setupTwoLineView(text1, text2)
        }
    }

}