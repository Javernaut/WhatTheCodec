package com.javernaut.whatthecodec.presentation.audio.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.domain.AudioStream
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
        containerView.codecName.setupTwoLineView(R.string.page_audio_codec_name, stream.codecName)
    }

}