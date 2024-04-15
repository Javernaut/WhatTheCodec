package com.javernaut.whatthecodec.home.presentation.model

data class ScreenState(
    val videoPage: VideoPage?,
    val audioPage: AudioPage?,
    val subtitlesPage: SubtitlesPage?
) {
    val availableTabs = buildList {
        if (videoPage != null) {
            add(AvailableTab.VIDEO)
        }
        if (audioPage != null) {
            add(AvailableTab.AUDIO)
        }
        if (subtitlesPage != null) {
            add(AvailableTab.SUBTITLES)
        }
    }
}
