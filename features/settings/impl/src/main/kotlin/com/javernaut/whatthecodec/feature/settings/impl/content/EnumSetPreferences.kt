package com.javernaut.whatthecodec.feature.settings.impl.content

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.EnumSet

internal inline fun <reified E : Enum<E>> DataStore<Preferences>.enumSetFlow(
    key: Preferences.Key<Set<String>>
): Flow<Set<E>> = data.map {
    it[key]?.mapTo(
        // Mapping into an EnumSet, as more efficient set implementation for enums
        mutableEnumSet()
    ) {
        enumValueOf(it)
    }
    // If nothing is present in DataStore, we treat it as 'everything is enabled'
        ?: completeEnumSet()
}

internal suspend fun DataStore<Preferences>.saveEnumSet(
    key: Preferences.Key<Set<String>>,
    enumSet: Set<Any>
) {
    edit { settings ->
        settings[key] = enumSet.mapTo(
            mutableSetOf()
        ) {
            it.toString()
        }
    }
}

inline fun <reified E : Enum<E>> mutableEnumSet(): EnumSet<E> {
    return EnumSet.noneOf(E::class.java)
}

inline fun <reified E : Enum<E>> completeEnumSet(): EnumSet<E> {
    return EnumSet.allOf(E::class.java)
}
