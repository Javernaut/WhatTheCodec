package com.javernaut.whatthecodec.presentation.root.ui

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.audio.ui.AudioPageFragment
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.AvailableTab
import com.javernaut.whatthecodec.presentation.stream.BasePageFragment
import com.javernaut.whatthecodec.presentation.subtitle.ui.SubtitlePageFragment
import com.javernaut.whatthecodec.presentation.video.ui.VideoPageFragment

class RootPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    var availableTabs: List<AvailableTab> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemId(position: Int): Long {
        return availableTabs[position].ordinal.toLong()
    }

    override fun containsItem(itemId: Long): Boolean {
        return availableTabs.contains(
                AvailableTab.values()[itemId.toInt()]
        )
    }

    override fun getItemCount() = availableTabs.size

    override fun createFragment(position: Int): BasePageFragment {
        return when (availableTabs[position]) {
            AvailableTab.VIDEO -> VideoPageFragment()
            AvailableTab.AUDIO -> AudioPageFragment()
            AvailableTab.SUBTITLES -> SubtitlePageFragment()
        }
    }

    val tabConfigurationStrategy = TabLayoutMediator.TabConfigurationStrategy { tab, position ->
        tab.setText(when (availableTabs[position]) {
            AvailableTab.VIDEO -> R.string.tab_video
            AvailableTab.AUDIO -> R.string.tab_audio
            AvailableTab.SUBTITLES -> R.string.tab_subtitles
        })
    }
}
