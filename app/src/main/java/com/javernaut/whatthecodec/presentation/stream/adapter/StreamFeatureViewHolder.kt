package com.javernaut.whatthecodec.presentation.stream.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.javernaut.whatthecodec.presentation.stream.model.StreamFeature
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_stream_feature.view.*

class StreamFeatureViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bindTo(streamFeature: StreamFeature) {
        containerView.text1.setText(streamFeature.title)
        containerView.text2.text = streamFeature.description
    }

}