package com.javernaut.whatthecodec.presentation.subtitle.ui

import androidx.lifecycle.Observer
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.domain.SubtitleStream
import com.javernaut.whatthecodec.presentation.stream.BasePageFragment
import com.javernaut.whatthecodec.presentation.stream.helper.DispositionHelper
import com.javernaut.whatthecodec.presentation.stream.helper.LanguageHelper
import com.javernaut.whatthecodec.presentation.stream.model.Stream
import com.javernaut.whatthecodec.presentation.stream.model.StreamFeature

class SubtitlePageFragment : BasePageFragment() {
    override fun onSubscribeToViewModel() {
        mediaFileViewModel.subtitleStreamsLiveData.observe(this, Observer {
            displayStreams(it.map { subtitleStream ->
                Stream(subtitleStream.index, subtitleStream.title, getFeaturesList(subtitleStream))
            })
        })
    }

    private fun getFeaturesList(stream: SubtitleStream): List<StreamFeature> =
            mutableListOf<StreamFeature>().apply {
                add(StreamFeature(R.string.page_audio_codec_name, stream.codecName))

                val language = LanguageHelper.getDisplayName(stream.language)
                if (language != null) {
                    add(StreamFeature(R.string.page_stream_language, language))
                }

                if (DispositionHelper.isDisplayable(stream.disposition)) {
                    add(StreamFeature(
                            R.string.page_stream_disposition,
                            DispositionHelper.toString(stream.disposition, resources))
                    )
                }
            }
}