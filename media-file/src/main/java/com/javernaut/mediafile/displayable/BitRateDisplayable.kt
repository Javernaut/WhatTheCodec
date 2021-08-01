package com.javernaut.mediafile.displayable

import android.content.res.Resources
import com.javernaut.mediafile.BitRate
import com.javernaut.mediafile.R
import java.text.DecimalFormat
import kotlin.math.roundToInt

fun BitRate.toDisplayable(resources: Resources): String? {
    return when {
        this <= 0 -> null
        this < 1000 -> format(this.toFloat(), R.string.media_file_bitrate_bps, resources)
        else -> {
            val kBitRate = this / 1000f
            if (kBitRate < 1000) {
                format(kBitRate, R.string.media_file_bitrate_kbps, resources)
            } else {
                val mBitRate = kBitRate / 1000f
                if (mBitRate < 1000) {
                    format(mBitRate, R.string.media_file_bitrate_mbps, resources)
                } else {
                    val gBitRate = mBitRate / 1000f
                    format(gBitRate, R.string.media_file_bitrate_gbps, resources)
                }
            }
        }
    }
}

private fun format(number: Float, postfixResId: Int, resources: Resources): String {
    val formattedNumber = if (number >= 100) {
        number.roundToInt().toString()
    } else {
        DecimalFormat("0.#").format(number)
    }
    return "$formattedNumber ${resources.getString(postfixResId)}"
}