package com.javernaut.whatthecodec.presentation.root.ui

import android.content.res.Resources
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.video.ui.VideoPageFragment

class RootPagerAdapter(fm: FragmentManager, private val resources: Resources) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int) = VideoPageFragment()

    override fun getPageTitle(position: Int) = when (position) {
        POSITION_VIDEO -> resources.getString(R.string.tab_video)
        else -> null
    }

    // TODO This value is variable actually. It depends on a particular video file.
    override fun getCount() = 1

    companion object {
        private const val POSITION_VIDEO = 0
    }
}