package com.javernaut.whatthecodec

import android.graphics.Point
import android.view.WindowManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlin.math.min

class VideoInfoViewModelFactory(windowManager: WindowManager) : ViewModelProvider.Factory {

    private val frameFullWidth = Point().let {
        windowManager.defaultDisplay.getSize(it)
        min(it.x, it.y)
    }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return VideoInfoViewModel(frameFullWidth) as T
    }
}