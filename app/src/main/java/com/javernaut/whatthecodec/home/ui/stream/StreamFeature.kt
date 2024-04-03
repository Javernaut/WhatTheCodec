package com.javernaut.whatthecodec.home.ui.stream

import android.content.res.Resources
import android.preference.PreferenceManager
import androidx.annotation.ArrayRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.javernaut.whatthecodec.compose.preference.getStringSetAsState
import io.github.javernaut.mediafile.MediaStream

interface StreamFeature<T : MediaStream> {
    val key: Int
    val title: Int
    fun getValue(stream: T, resources: Resources): String?
}

@Composable
fun <T : StreamFeature<*>> getFilteredStreamFeatures(
    @ArrayRes defaultValueResId: Int,
    preferenceKey: String,
    allValues: List<T>
): List<T> {
    val applicationContext = LocalContext.current.applicationContext
    val defaultSharedPreferences = remember {
        PreferenceManager.getDefaultSharedPreferences(applicationContext)
    }
    val entriesCodes = stringArrayResource(id = defaultValueResId).toSet()

    val actualFeaturesToDisplay by defaultSharedPreferences.getStringSetAsState(
        preferenceKey,
        entriesCodes
    )

    return actualFeaturesToDisplay?.let { set ->
        allValues.filter {
            set.contains(stringResource(id = it.key))
        }
    } ?: emptyList()
}
