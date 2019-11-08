package com.javernaut.whatthecodec.presentation.video.ui.view

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.sqrt

class FrameDisplayingView(context: Context, attrs: AttributeSet) : RecyclerView(context, attrs) {

    private val commonSpacing = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            2f,
            context.resources.displayMetrics).toInt()

    init {
        setPadding(commonSpacing, commonSpacing, commonSpacing, commonSpacing)
    }

    private val framesAdapter = Adapter(commonSpacing).also {
        adapter = it
    }

    fun setFrames(frames: Array<Bitmap>) {
        val childFramesPerRow = sqrt(frames.size.toDouble()).toInt()

        layoutManager = GridLayoutManager(context, childFramesPerRow)

        framesAdapter.frames = frames
    }

    private class Adapter(private val commonSpacing: Int) : RecyclerView.Adapter<ViewHolder>() {

        var frames: Array<Bitmap> = emptyArray()
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

            layoutParams.setMargins(commonSpacing, commonSpacing, commonSpacing, commonSpacing)

            val itemView = ImageView(parent.context)

            itemView.adjustViewBounds = true
            itemView.layoutParams = layoutParams

            return ViewHolder(itemView)
        }

        override fun getItemCount() = frames.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(frames[position])
        }

    }

    private class ViewHolder(private val imageView: ImageView) : RecyclerView.ViewHolder(imageView) {

        fun bind(bitmap: Bitmap) {
            imageView.setImageBitmap(bitmap)
        }

    }
}
