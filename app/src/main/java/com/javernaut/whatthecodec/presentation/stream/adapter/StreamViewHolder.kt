package com.javernaut.whatthecodec.presentation.stream.adapter

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.stream.model.Stream
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_stream.view.*

class StreamViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    private val subAdapter = StreamFeaturesAdapter()

    init {
        val layoutManager = GridLayoutManager(containerView.context, 2)
        containerView.streamFeatures.layoutManager = layoutManager
        containerView.streamFeatures.adapter = subAdapter
    }

    fun bindTo(stream: Stream) {
        containerView.streamTitle.apply {
            setText(R.string.page_stream_title_prefix)
            append(stream.index.toString())
            if (stream.title != null) {
                append(" - " + stream.title)
            }
        }

        subAdapter.items = stream.features
    }

}