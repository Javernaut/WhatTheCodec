package com.javernaut.whatthecodec.presentation.stream.adapter.animator

import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup

class HeightAnimator(private val viewToAnimate: View) {

    private var lastHeightAnimator: ValueAnimator? = null

    fun reset() {
        lastHeightAnimator?.cancel()
        lastHeightAnimator = null
    }

    fun setExpanded(isExpanded: Boolean) {
        reset()

        val layoutParams = viewToAnimate.layoutParams
        layoutParams.height = if (isExpanded) {
            ViewGroup.LayoutParams.WRAP_CONTENT
        } else {
            0
        }
        viewToAnimate.layoutParams = layoutParams
    }

    fun animateExpandedTo(isExpanded: Boolean) {
        lastHeightAnimator?.cancel()

        val endHeight = if (isExpanded) {
            viewToAnimate.measure(
                    View.MeasureSpec.makeMeasureSpec(viewToAnimate.measuredWidth, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            val resultHeight = viewToAnimate.measuredHeight
            val layoutParams = viewToAnimate.layoutParams
            layoutParams.height = 0
            viewToAnimate.layoutParams = layoutParams
            resultHeight
        } else {
            0
        }

        val startHeight = lastHeightAnimator?.animatedValue as? Int ?: if (isExpanded) {
            0
        } else {
            viewToAnimate.measuredHeight
        }

        lastHeightAnimator = ValueAnimator.ofInt(startHeight, endHeight).apply {
            addUpdateListener { valueAnimator ->
                val animatedValue = valueAnimator.animatedValue as Int
                val layoutParams = viewToAnimate.layoutParams
                layoutParams.height = animatedValue
                viewToAnimate.layoutParams = layoutParams
            }
            start()
        }
    }
}
