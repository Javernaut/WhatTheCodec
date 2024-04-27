package com.javernaut.whatthecodec.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

object TinyActivityCompat {
    fun needRequestReadStoragePermission(context: Context) =
        Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2 &&
                context.checkPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Process.myPid(),
                    Process.myUid()
                ) != PackageManager.PERMISSION_GRANTED

    fun requestReadStoragePermission(launcher: ActivityResultLauncher<String>) {
        launcher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    fun requestPermissionContract() = ActivityResultContracts.RequestPermission()
}
