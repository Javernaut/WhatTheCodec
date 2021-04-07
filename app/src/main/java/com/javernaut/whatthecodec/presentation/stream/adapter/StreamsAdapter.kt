package com.javernaut.whatthecodec.presentation.stream.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.RecyclerView
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.stream.model.StreamCard

@OptIn(ExperimentalFoundationApi::class)
class StreamsAdapter : RecyclerView.Adapter<StreamCardViewHolder>() {

    var streamCards = emptyList<StreamCard>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StreamCardViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_stream, parent, false)
        return StreamCardViewHolder(itemView as ComposeView)
    }

    override fun onBindViewHolder(holder: StreamCardViewHolder, position: Int) {
        holder.bindTo(streamCards[position])
    }

    override fun getItemCount() = streamCards.size
}
