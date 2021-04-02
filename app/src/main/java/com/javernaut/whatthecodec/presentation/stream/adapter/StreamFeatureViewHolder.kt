package com.javernaut.whatthecodec.presentation.stream.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.stream.model.StreamFeature

class StreamFeatureViewHolder(private val itemComposeView: ComposeView) :
    RecyclerView.ViewHolder(itemComposeView) {

    private lateinit var streamFeature: StreamFeature

    init {
        itemComposeView.setOnLongClickListener {
            showPopupMenu()
            true
        }
    }

    fun bindTo(streamFeature: StreamFeature) {
        this.streamFeature = streamFeature

        itemComposeView.setContent {
            Column(
                Modifier
                    .clickable {
                        showPopupMenu()
                    }
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 58.dp)
            ) {
                Text(stringResource(id = streamFeature.title))
                Text(streamFeature.description)
            }
        }
    }

    private fun showPopupMenu() {
        val popupMenu = PopupMenu(itemComposeView.context, itemComposeView)

        val firstItemText: Spannable = SpannableString(streamFeature.description)
        val accentColor = itemComposeView.run {
            val typedValue = TypedValue()
            context.theme.resolveAttribute(R.attr.colorAccent, typedValue, true)
            typedValue.data
        }
        firstItemText.setSpan(
            ForegroundColorSpan(accentColor),
            0, firstItemText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val firstItem = popupMenu.menu.add(firstItemText)
        firstItem.isEnabled = false

        popupMenu.menu.add(R.string.stream_copy).setOnMenuItemClickListener {
            copyTextToClipboard();
            true
        }
        popupMenu.show()
    }

    private fun copyTextToClipboard() {
        val clipboardManager = itemComposeView.context.getSystemService<ClipboardManager>()!!
        val label = itemComposeView.resources.getString(streamFeature.title)
        clipboardManager.setPrimaryClip(
            ClipData.newPlainText(label, streamFeature.description)
        )
        val toastMessage = itemComposeView.resources.getString(
            R.string.stream_text_copied_pattern, streamFeature.description
        )
        Toast.makeText(itemComposeView.context, toastMessage, Toast.LENGTH_SHORT).show()
    }
}
