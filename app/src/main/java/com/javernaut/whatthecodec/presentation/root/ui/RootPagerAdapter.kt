package com.javernaut.whatthecodec.presentation.root.ui

import android.content.res.Resources
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.audio.ui.AudioPageFragment
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.AvailableTab
import com.javernaut.whatthecodec.presentation.video.ui.VideoPageFragment

class RootPagerAdapter(fm: FragmentManager, private val resources: Resources) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    var availableTabs: List<AvailableTab> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItem(position: Int) = when (availableTabs[position]) {
        AvailableTab.VIDEO -> VideoPageFragment()
        AvailableTab.AUDIO -> AudioPageFragment()
        AvailableTab.SUBTITLES -> TODO()
    }

    override fun getPageTitle(position: Int) = resources.getString(
            when (availableTabs[position]) {
                AvailableTab.VIDEO -> R.string.tab_video
                AvailableTab.AUDIO -> R.string.tab_audio
                AvailableTab.SUBTITLES -> R.string.tab_subtitles
            }
    )

    override fun getCount() = availableTabs.size

}