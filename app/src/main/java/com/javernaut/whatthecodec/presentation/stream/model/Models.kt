package com.javernaut.whatthecodec.presentation.stream.model

import android.content.res.Resources
import com.javernaut.whatthecodec.R
import io.github.javernaut.mediafile.BasicStreamInfo
import io.github.javernaut.mediafile.displayable.displayableLanguage
import io.github.javernaut.mediafile.displayable.getDisplayableDisposition

class StreamFeature(
    val title: Int,
    val description: String
)

/**
 * Creates a List<StreamFeature> object with index and title of a given [BasicStreamInfo].
 * Also language and disposition elements are added to the end of the list.
 */
fun makeStream(
    basicStreamInfo: BasicStreamInfo,
    resources: Resources,
    filler: MutableList<StreamFeature>.() -> Unit
): List<StreamFeature> {
    return mutableListOf<StreamFeature>().apply {

        filler(this)

        basicStreamInfo.displayableLanguage?.let {
            add(StreamFeature(R.string.page_stream_language, it))
        }

        basicStreamInfo.getDisplayableDisposition(resources)?.let {
            add(StreamFeature(R.string.page_stream_disposition, it))
        }
    }
}
