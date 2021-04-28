package com.javernaut.whatthecodec.presentation.stream.model

import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.domain.BasicStreamInfo
import com.javernaut.whatthecodec.presentation.stream.helper.DispositionHelper
import com.javernaut.whatthecodec.presentation.stream.helper.LanguageHelper

class StreamCard(
        val title: String,
        val features: List<StreamFeature>,
        var isExpanded: Boolean = true
)

/**
 * Creates a [StreamCard] object with index and title of a given [BasicStreamInfo].
 * Also language and disposition elements are added to the end of the list.
 */
@Composable
fun makeStream(basicStreamInfo: BasicStreamInfo,
               filler: @Composable MutableList<StreamFeature>.() -> Unit): StreamCard {
    return StreamCard(
            makeCardTitle(basicStreamInfo.index, basicStreamInfo.title),
            mutableListOf<StreamFeature>().apply {

                filler(this)

                val language = LanguageHelper.getDisplayName(basicStreamInfo.language)
                if (language != null) {
                    add(StreamFeature(R.string.page_stream_language, language))
                }

                if (DispositionHelper.isDisplayable(basicStreamInfo.disposition)) {
                    add(StreamFeature(
                            R.string.page_stream_disposition,
                            DispositionHelper.toString(basicStreamInfo.disposition))
                    )
                }
            }
    )
}

@Composable
private fun makeCardTitle(index: Int, title: String?): String {
    val prefix = stringResource(id = R.string.page_stream_title_prefix)
    return if (title == null) {
        prefix + index
    } else {
        "$prefix$index - $title"
    }
}
