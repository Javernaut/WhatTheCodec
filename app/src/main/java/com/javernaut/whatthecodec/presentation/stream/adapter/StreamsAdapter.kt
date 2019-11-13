package com.javernaut.whatthecodec.presentation.stream.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.stream.model.Stream

class StreamsAdapter : RecyclerView.Adapter<StreamViewHolder>() {

    var streams = emptyList<Stream>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val expandStatusChangeListener = object : StreamViewHolder.OnExpandStatusChangeListener {
        override fun onExpandStatusChange(viewHolder: StreamViewHolder, isExpanded: Boolean) {
            notifyItemChanged(viewHolder.adapterPosition, isExpanded)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StreamViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_stream, parent, false)
        return StreamViewHolder(itemView, expandStatusChangeListener)
    }

    override fun onBindViewHolder(holder: StreamViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            val isExpanded = payloads.first() as? Boolean == true
            holder.animateList(isExpanded)
        }
    }

    override fun onBindViewHolder(holder: StreamViewHolder, position: Int) {
        holder.bindTo(streams[position])
    }

    override fun getItemCount() = streams.size

}