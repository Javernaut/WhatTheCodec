package com.javernaut.whatthecodec.presentation.stream.adapter

import android.view.View
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.recyclerview.widget.RecyclerView
import com.javernaut.whatthecodec.presentation.stream.adapter.animator.HeightAnimator
import com.javernaut.whatthecodec.presentation.stream.model.StreamCard
import com.javernaut.whatthecodec.presentation.stream.model.StreamFeature
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_stream.view.*

@ExperimentalFoundationApi
class StreamCardViewHolder(
    override val containerView: View,
    listener: OnExpandStatusChangeListener
) :
    RecyclerView.ViewHolder(containerView), LayoutContainer {

    private lateinit var item: StreamCard

    private val subListHeightAnimator = HeightAnimator(containerView.streamFeatures)

    init {
        containerView.expandToggle.setOnClickListener {
            item.isExpanded = !item.isExpanded
            listener.onExpandStatusChange(this, item.isExpanded)
        }
    }

    fun bindTo(streamCard: StreamCard) {
        this.item = streamCard

        containerView.streamTitle.text = streamCard.title

        containerView.streamFeatures.setContent {
            WhatTheCodecTheme {
                StreamFeaturesGrid(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    features = streamCard.features
                )
            }
        }

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

@ExperimentalFoundationApi
@Composable
fun StreamFeaturesGrid(
    modifier: Modifier = Modifier,
    features: List<StreamFeature>
) {
    FixedGrid(modifier, 2) {
        features.forEach {
            StreamFeatureItem(streamFeature = it)
        }
    }
}

@Composable
fun FixedGrid(
    modifier: Modifier = Modifier,
    columns: Int = 1,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        val columnWidth = constraints.maxWidth / columns

        val placables = measurables.map {
            it.measure(Constraints.fixedWidth(columnWidth))
        }

        val chunkedPlacables = placables.chunked(columns)
        val maxHeights = chunkedPlacables.map { it.maxByOrNull { it.height }!!.height }
        val dstHeight = maxHeights.sum()

        var runningY = 0
        layout(constraints.maxWidth, dstHeight) {
            chunkedPlacables.forEachIndexed { index, list ->
                var runningX = 0
                list.forEach {
                    it.placeRelative(runningX, runningY)
                    runningX += columnWidth
                }
                runningY += maxHeights[index]
            }
        }
    }
}
