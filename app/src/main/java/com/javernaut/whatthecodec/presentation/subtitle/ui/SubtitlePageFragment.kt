package com.javernaut.whatthecodec.presentation.subtitle.ui

import android.os.Bundle
import androidx.lifecycle.Observer
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.domain.SubtitleStream
import com.javernaut.whatthecodec.presentation.stream.BasePageFragment
import com.javernaut.whatthecodec.presentation.stream.model.StreamFeature
import com.javernaut.whatthecodec.presentation.stream.model.makeStream

class SubtitlePageFragment : BasePageFragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mediaFileViewModel.subtitleStreamsLiveData.observe(viewLifecycleOwner, Observer {
            displayStreams(it.map(::convertStream))
        })
    }

    private fun convertStream(subtitleStream: SubtitleStream) =
            makeStream(subtitleStream.basicInfo, resources) {
                add(StreamFeature(R.string.page_subtitle_codec_name, subtitleStream.basicInfo.codecName))
            }
}