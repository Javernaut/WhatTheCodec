package com.javernaut.whatthecodec.presentation.audio.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.javernaut.whatthecodec.R
import java.text.DecimalFormat
import kotlin.math.roundToInt

object BitRateHelper {
    @Composable
    fun toString(bitRate: Long): String {
        return if (bitRate < 1000) {
            format(bitRate.toFloat(), R.string.stream_bitrate_bps)
        } else {
            val kBitRate = bitRate / 1000f
            if (kBitRate < 1000) {
                format(kBitRate, R.string.stream_bitrate_kbps)
            } else {
                val mBitRate = kBitRate / 1000f
                if (mBitRate < 1000) {
                    format(mBitRate, R.string.stream_bitrate_mbps)
                } else {
                    val gBitRate = mBitRate / 1000f
                    format(gBitRate, R.string.stream_bitrate_gbps)
                }
            }
        }
    }

    @Composable
    private fun format(number: Float, postfixResId: Int): String {
        val formattedNumber = if (number >= 100) {
            number.roundToInt().toString()
        } else {
            DecimalFormat("0.#").format(number)
        }
        return "$formattedNumber ${stringResource(id = postfixResId)}"
    }
}
