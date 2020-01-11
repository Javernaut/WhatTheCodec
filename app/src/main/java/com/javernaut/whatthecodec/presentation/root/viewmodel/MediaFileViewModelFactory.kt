package com.javernaut.whatthecodec.presentation.root.viewmodel

import androidx.activity.ComponentActivity
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class MediaFileViewModelFactory(
        activity: ComponentActivity,
        private val desiredFrameWidth: Int
) : AbstractSavedStateViewModelFactory(activity, null) {

    private val configProvider = MediaFileProviderImpl(activity)

    override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
        return MediaFileViewModel(desiredFrameWidth, configProvider, handle) as T
    }
}