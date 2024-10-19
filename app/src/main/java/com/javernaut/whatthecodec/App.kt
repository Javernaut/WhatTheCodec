package com.javernaut.whatthecodec

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import io.github.javernaut.mediafile.factory.MediaFileFactory

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        MediaFileFactory.initWith(this)
    }
}
