package com.javernaut.whatthecodec.presentation.audio.ui.streamfeature

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.javernaut.whatthecodec.R

class StreamFeaturesAdapter : RecyclerView.Adapter<StreamFeatureViewHolder>() {

    var items = emptyList<StreamFeature>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StreamFeatureViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.inline_simple_list_item_2, parent, false)
        return StreamFeatureViewHolder(itemView)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: StreamFeatureViewHolder, position: Int) {
        holder.bindTo(items[position])
    }

}