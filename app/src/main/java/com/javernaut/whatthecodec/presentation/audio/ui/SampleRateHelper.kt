package com.javernaut.whatthecodec.presentation.audio.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.javernaut.whatthecodec.R
import java.text.DecimalFormat

object SampleRateHelper {
    @Composable
    fun toString(sampleRate: Int): String {
        val formattedSampleRate = DecimalFormat("0.#").format(sampleRate / 1000f)
        return formattedSampleRate + " " + stringResource(id = R.string.page_audio_sample_rate_postfix)
    }
}
