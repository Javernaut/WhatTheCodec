package com.javernaut.whatthecodec.presentation.audio.ui

import android.content.res.Resources
import com.javernaut.whatthecodec.R
import java.text.DecimalFormat

object BitRateHelper {
    fun toString(bitRate: Long, resources: Resources): String {
        return if (bitRate < 1000) {
            format(bitRate.toFloat(), R.string.stream_bitrate_bps, resources)
        } else {
            val kBitRate = bitRate / 1000f
            if (kBitRate < 1000) {
                format(kBitRate, R.string.stream_bitrate_kbps, resources)
            } else {
                val mBitRate = kBitRate / 1000f
                if (mBitRate < 1000) {
                    format(mBitRate, R.string.stream_bitrate_mbps, resources)
                } else {
                    val gBitRate = mBitRate / 1000f
                    format(gBitRate, R.string.stream_bitrate_gbps, resources)
                }
            }
        }
    }

    private fun format(number: Float, postfixResId: Int, resources: Resources): String {
        val formattedNumber = if (number >= 100) {
            number.toInt().toString()
        } else {
            DecimalFormat("0.#").format(number)
        }
        return "$formattedNumber ${resources.getString(postfixResId)}"
    }
}