package com.javernaut.whatthecodec.presentation.stream.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.stream.model.StreamFeature
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_stream_feature.view.*

class StreamFeatureViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    private lateinit var streamFeature: StreamFeature

    init {
        containerView.setOnLongClickListener {
            val popupMenu = PopupMenu(containerView.context, containerView)

            val firstItemText: Spannable = SpannableString(streamFeature.description)
            val accentColor = containerView.run {
                val typedValue = TypedValue()
                context.theme.resolveAttribute(R.attr.colorAccent, typedValue, true)
                typedValue.data
            }
            firstItemText.setSpan(ForegroundColorSpan(accentColor), 0, firstItemText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            val firstItem = popupMenu.menu.add(firstItemText)
            firstItem.isEnabled = false

            popupMenu.menu.add(R.string.stream_copy).setOnMenuItemClickListener {
                copyTextToClipboard();
                true
            }
            popupMenu.show()
            true
        }
    }

    fun bindTo(streamFeature: StreamFeature) {
        this.streamFeature = streamFeature;
        containerView.text1.setText(streamFeature.title)
        containerView.text2.text = streamFeature.description
    }

    private fun copyTextToClipboard() {
        val clipboardManager = containerView.context.getSystemService<ClipboardManager>()!!
        val label = containerView.resources.getString(streamFeature.title)
        clipboardManager.setPrimaryClip(
                ClipData.newPlainText(label, streamFeature.description)
        )
        val toastMessage = containerView.resources.getString(R.string.stream_text_copied_pattern, streamFeature.description)
        Toast.makeText(containerView.context, toastMessage, Toast.LENGTH_SHORT).show()
    }
}
