package com.javernaut.whatthecodec.home.presentation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.core.content.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Clipboard @Inject constructor(
    @ApplicationContext context: Context
) {
    private val clipboardManager = context.getSystemService<ClipboardManager>()!!

    fun copy(value: String) {
        clipboardManager.setPrimaryClip(
            ClipData.newPlainText(null, value)
        )
    }
}
