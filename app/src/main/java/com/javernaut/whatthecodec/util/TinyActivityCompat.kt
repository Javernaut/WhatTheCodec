package com.javernaut.whatthecodec.util

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process

object TinyActivityCompat {
    fun needRequestReadStoragePermission(context: Context) =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    context.checkPermission(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Process.myPid(),
                            Process.myUid()
                    ) != PackageManager.PERMISSION_GRANTED

    @TargetApi(Build.VERSION_CODES.M)
    fun requestReadStoragePermission(activity: Activity, requestCode: Int) {
        activity.requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), requestCode)
    }

    fun wasReadStoragePermissionGranted(permissions: Array<out String>, grantResults: IntArray): Boolean {
        val index = permissions.indexOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        return index >= 0 && grantResults[index] == PackageManager.PERMISSION_GRANTED
    }
}
