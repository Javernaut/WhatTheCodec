package com.javernaut.mediafile

import android.content.res.Resources
import androidx.annotation.StringRes

object DispositionHelper {
    fun isDisplayable(disposition: Int) = disposition != 0

    fun toString(disposition: Int, resources: Resources) =
        DispositionFeature.values().filter {
            disposition and it.mask != 0
        }.joinToString(separator = ", ") {
            resources.getString(it.stringIdRes)
        }

    /**
     * Class that maps a certain bit in a 'disposition' integer to a string res id.
     * Values for masks got from libavformat/avformat.h.
     */
    enum class DispositionFeature(val mask: Int, @StringRes val stringIdRes: Int) {
        DEFAULT(0x0001, R.string.stream_disposition_default),
        DUB(0x0002, R.string.stream_disposition_dub),
        ORIGINAL(0x0004, R.string.stream_disposition_original),
        COMMENT(0x0008, R.string.stream_disposition_comment),
        LYRICS(0x0010, R.string.stream_disposition_lyrics),
        KARAOKE(0x0020, R.string.stream_disposition_karaoke),
        FORCED(0x0040, R.string.stream_disposition_forced),
        HEARING_IMPAIRED(0x0080, R.string.stream_disposition_hearing_impaired),
        VISUAL_IMPAIRED(0x0100, R.string.stream_disposition_visual_impaired),
        CLEAN_EFFECTS(0x0200, R.string.stream_disposition_clean_effects),
        ATTACHED_PIC(0x0400, R.string.stream_disposition_attached_pic),
        TIMED_THUMBNAILS(0x0800, R.string.stream_disposition_timed_thumbnails)
    }
}
