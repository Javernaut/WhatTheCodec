package com.javernaut.whatthecodec.presentation.viewmodel

import android.app.Activity
import android.graphics.Point
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlin.math.min

class VideoInfoViewModelFactory(activity: Activity) : ViewModelProvider.Factory {

    private val frameFullWidth = Point().let {
        activity.windowManager.defaultDisplay.getSize(it)
        min(it.x, it.y)
    }

    private val configProvider = ConfigProviderImpl(activity)

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return VideoInfoViewModel(frameFullWidth, configProvider) as T
    }
}