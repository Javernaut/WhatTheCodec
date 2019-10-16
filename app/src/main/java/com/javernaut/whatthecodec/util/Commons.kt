package com.javernaut.whatthecodec.util

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.javernaut.whatthecodec.R

/**
 * Function to setup a view inflated from @layout/inline_simple_list_item_2
 */
fun View.setupTwoLineView(text1: Int, text2: String) {
    findViewById<TextView>(R.id.text1).setText(text1)
    findViewById<TextView>(R.id.text2).text = text2
}

inline fun ViewGroup.forEachChild(action: (View) -> Unit) {
    for (pos in 0 until childCount) {
        action(getChildAt(pos))
    }
}

fun View.setVisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}