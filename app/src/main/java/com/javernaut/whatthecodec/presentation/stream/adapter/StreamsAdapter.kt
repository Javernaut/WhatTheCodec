package com.javernaut.whatthecodec.presentation.stream.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.stream.model.StreamCard

class StreamsAdapter : RecyclerView.Adapter<StreamCardViewHolder>() {

    var streamCards = emptyList<StreamCard>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val expandStatusChangeListener = object : StreamCardViewHolder.OnExpandStatusChangeListener {
        override fun onExpandStatusChange(viewHolder: StreamCardViewHolder, isExpanded: Boolean) {
            notifyItemChanged(viewHolder.adapterPosition, isExpanded)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StreamCardViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_stream, parent, false)
        return StreamCardViewHolder(itemView, expandStatusChangeListener)
    }

    override fun onBindViewHolder(holder: StreamCardViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            val isExpanded = payloads.first() as? Boolean == true
            holder.animateList(isExpanded)
        }
    }

    override fun onBindViewHolder(holder: StreamCardViewHolder, position: Int) {
        holder.bindTo(streamCards[position])
    }

    override fun getItemCount() = streamCards.size

}
