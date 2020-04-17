package com.javernaut.whatthecodec.presentation.video.ui.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.Frame
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.FrameMetrics

class FramesAdapter : RecyclerView.Adapter<FrameViewHolder>() {

    private var frames: List<Frame> = emptyList()
    private lateinit var frameMetrics: FrameMetrics

    fun setFrames(newFrames: List<Frame>, frameMetrics: FrameMetrics) {
        this.frameMetrics = frameMetrics
        // TODO How about DiffUtil here?
        this.frames = newFrames
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FrameViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_frame, parent, false)

        return FrameViewHolder(itemView)
    }

    override fun getItemCount() = frames.size

    override fun onBindViewHolder(holder: FrameViewHolder, position: Int) {
        val itemView = holder.itemView

        // Forcing the itemView width and height
        val layoutParams = itemView.layoutParams
        layoutParams.width = frameMetrics.width
        layoutParams.height = frameMetrics.height
        itemView.layoutParams = layoutParams

        holder.bind(frames[position])
    }
}