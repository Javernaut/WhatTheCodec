package com.javernaut.whatthecodec.presentation.audio.ui

import android.content.res.Resources
import com.javernaut.whatthecodec.R
import java.text.DecimalFormat

object SampleRateHelper {
    fun toString(sampleRate: Int, resources: Resources): String {
        val formattedSampleRate = DecimalFormat("0.#").format(sampleRate / 1000f)
        return formattedSampleRate + " " + resources.getString(R.string.page_audio_sample_rate_postfix)
    }
}
