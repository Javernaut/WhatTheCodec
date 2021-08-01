package com.javernaut.mediafile

import android.content.res.Resources
import java.text.DecimalFormat

object SampleRateHelper {
    fun toString(sampleRate: Int, resources: Resources): String {
        val formattedSampleRate = DecimalFormat("0.#").format(sampleRate / 1000f)
        return formattedSampleRate + " " + resources.getString(R.string.page_audio_sample_rate_postfix)
    }
}
