package com.javernaut.whatthecodec.presentation.root.viewmodel

import android.graphics.Point
import androidx.activity.ComponentActivity
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlin.math.min

class VideoInfoViewModelFactory(activity: ComponentActivity) : AbstractSavedStateViewModelFactory(activity, null) {
    private val frameFullWidth = Point().let {
        activity.windowManager.defaultDisplay.getSize(it)
        min(it.x, it.y)
    }

    private val configProvider = ConfigProviderImpl(activity)

    override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
        return VideoInfoViewModel(frameFullWidth, configProvider, handle) as T
    }
}