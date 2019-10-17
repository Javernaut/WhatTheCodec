package com.javernaut.whatthecodec.presentation.audio.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.domain.AudioStream
import com.javernaut.whatthecodec.presentation.audio.ui.streamfeature.StreamFeature
import com.javernaut.whatthecodec.presentation.audio.ui.streamfeature.StreamFeaturesAdapter
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

    private val subAdapter = StreamFeaturesAdapter()

    init {
        val layoutManager = GridLayoutManager(containerView.context, 2)
        containerView.streamFeatures.layoutManager = layoutManager
        containerView.streamFeatures.adapter = subAdapter
    }

    fun bindTo(stream: AudioStream) {
        containerView.streamTitle.apply {
            setText(R.string.page_audio_stream_title_prefix)
            append(stream.index.toString())
            if (stream.title != null) {
                append(" - " + stream.title)
            }
        }

        subAdapter.items = getFeaturesList(stream)
    }

    private fun getFeaturesList(stream: AudioStream): List<StreamFeature> =
            mutableListOf<StreamFeature>().apply {
                add(StreamFeature(R.string.page_audio_codec_name, stream.codecName))
                add(StreamFeature(R.string.page_audio_bit_rate, BitRateHelper.toString(stream.bitRate, containerView.resources)))
                add(StreamFeature(R.string.page_audio_channels, stream.channels.toString()))

                if (stream.channelLayout != null) {
                    add(StreamFeature(R.string.page_audio_channel_layout, stream.channelLayout))
                }
                if (stream.sampleFormat != null) {
                    add(StreamFeature(R.string.page_audio_sample_format, stream.sampleFormat))
                }

                add(StreamFeature(R.string.page_audio_sample_rate, SampleRateHelper.toString(stream.sampleRate, containerView.resources)))

                if (stream.language != null) {
                    add(StreamFeature(R.string.page_audio_stream_language, stream.language.capitalize()))
                }

                if (DispositionHelper.isDisplayable(stream.disposition)) {
                    add(StreamFeature(
                            R.string.page_audio_stream_disposition,
                            DispositionHelper.toString(stream.disposition, containerView.resources))
                    )
                }
            }

}