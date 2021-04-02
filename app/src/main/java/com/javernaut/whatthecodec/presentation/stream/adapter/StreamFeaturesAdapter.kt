package com.javernaut.whatthecodec.presentation.stream.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.RecyclerView
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.stream.model.StreamFeature

class StreamFeaturesAdapter : RecyclerView.Adapter<StreamFeatureViewHolder>() {

    var items = emptyList<StreamFeature>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StreamFeatureViewHolder {
//        val itemView = LayoutInflater.from(parent.context)
//                .inflate(R.layout.item_stream_feature, parent, false)
        return StreamFeatureViewHolder(ComposeView(parent.context))
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: StreamFeatureViewHolder, position: Int) {
        holder.bindTo(items[position])
    }

}
