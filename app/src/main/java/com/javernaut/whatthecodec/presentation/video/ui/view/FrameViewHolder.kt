package com.javernaut.whatthecodec.presentation.video.ui.view

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.ActualFrame
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.DecodingErrorFrame
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.Frame
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.LoadingFrame
import com.javernaut.whatthecodec.util.setVisible
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_frame.view.*

class FrameViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(frame: Frame) {
        containerView.actualFrameView.setImageBitmap(
                (frame as? ActualFrame)?.frameData
        )
        setVisibilities(
                frame == LoadingFrame,
                frame is ActualFrame,
                frame == DecodingErrorFrame
        )
    }

    private fun setVisibilities(progress: Boolean = false, actualFrame: Boolean = false, decodingError: Boolean = false) {
        containerView.progressView.setVisible(progress)
        containerView.actualFrameView.setVisible(actualFrame)
        containerView.decodingErrorView.setVisible(decodingError)
    }
}
