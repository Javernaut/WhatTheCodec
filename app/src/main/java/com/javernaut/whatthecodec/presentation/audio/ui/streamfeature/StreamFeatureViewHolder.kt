package com.javernaut.whatthecodec.presentation.audio.ui.streamfeature

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.inline_simple_list_item_2.view.*

class StreamFeatureViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bindTo(streamFeature: StreamFeature) {
        containerView.text1.setText(streamFeature.title)
        containerView.text2.text = streamFeature.description
    }

}