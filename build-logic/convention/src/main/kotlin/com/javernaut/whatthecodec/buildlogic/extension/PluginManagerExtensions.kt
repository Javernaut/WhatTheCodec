package com.javernaut.whatthecodec.buildlogic.extension

import org.gradle.api.plugins.PluginManager
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderConvertible
import org.gradle.plugin.use.PluginDependency

internal fun PluginManager.apply(pluginProvider: Provider<PluginDependency>) {
    apply(pluginProvider.get().pluginId)
}

internal fun PluginManager.apply(pluginProvider: ProviderConvertible<PluginDependency>) {
    apply(pluginProvider.asProvider())
}

internal fun PluginManager.hasPlugin(pluginProvider: Provider<PluginDependency>): Boolean {
    return hasPlugin(pluginProvider.get().pluginId)
}
