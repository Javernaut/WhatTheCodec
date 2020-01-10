package com.javernaut.whatthecodec.presentation.stream.model

import android.content.res.Resources
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.domain.BasicStreamInfo
import com.javernaut.whatthecodec.presentation.stream.helper.DispositionHelper
import com.javernaut.whatthecodec.presentation.stream.helper.LanguageHelper

class Stream(
        val index: Int,
        val title: String?,
        val features: List<StreamFeature>,
        var isExpanded: Boolean = true
)

/**
 * Creates a [Stream] object with index and title of a given [BasicStreamInfo].
 * Also language and disposition elements are added to the end of the list.
 */
inline fun makeStream(basicStreamInfo: BasicStreamInfo, resources: Resources, filler: MutableList<StreamFeature>.() -> Unit): Stream {
    return Stream(basicStreamInfo.index, basicStreamInfo.title,
            mutableListOf<StreamFeature>().apply {

                filler(this)

                val language = LanguageHelper.getDisplayName(basicStreamInfo.language)
                if (language != null) {
                    add(StreamFeature(R.string.page_stream_language, language))
                }

                if (DispositionHelper.isDisplayable(basicStreamInfo.disposition)) {
                    add(StreamFeature(
                            R.string.page_stream_disposition,
                            DispositionHelper.toString(basicStreamInfo.disposition, resources))
                    )
                }
            }
    )
}