package com.javernaut.whatthecodec.presentation.stream.adapter

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.javernaut.whatthecodec.presentation.stream.adapter.animator.HeightAnimator
import com.javernaut.whatthecodec.presentation.stream.model.StreamCard
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_stream.view.*

class StreamCardViewHolder(override val containerView: View, listener: OnExpandStatusChangeListener) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    private lateinit var item: StreamCard

    private val subAdapter = StreamFeaturesAdapter()

    private val subListHeightAnimator = HeightAnimator(containerView.streamFeatures)

    init {
        val layoutManager = GridLayoutManager(containerView.context, 2)
        containerView.streamFeatures.layoutManager = layoutManager
        containerView.streamFeatures.adapter = subAdapter

        containerView.expandToggle.setOnClickListener {
            item.isExpanded = !item.isExpanded
            listener.onExpandStatusChange(this, item.isExpanded)
        }
    }

    fun bindTo(streamCard: StreamCard) {
        this.item = streamCard

        containerView.streamTitle.text = streamCard.title

        subAdapter.items = streamCard.features

        subListHeightAnimator.setExpanded(streamCard.isExpanded)

        containerView.expandToggle.rotation = if (streamCard.isExpanded) 0f else 180f
        containerView.streamFeatures.alpha = if (streamCard.isExpanded) 1f else 0f
    }

    fun animateList(isExpanded: Boolean) {
        containerView.streamFeatures
                .animate()
                .alpha(if (isExpanded) 1f else 0f)

        containerView.expandToggle
                .animate()
                .rotation(if (isExpanded) 0f else 180f)

        subListHeightAnimator.animateExpandedTo(isExpanded)
    }

    interface OnExpandStatusChangeListener {
        fun onExpandStatusChange(viewHolder: StreamCardViewHolder, isExpanded: Boolean)
    }
}