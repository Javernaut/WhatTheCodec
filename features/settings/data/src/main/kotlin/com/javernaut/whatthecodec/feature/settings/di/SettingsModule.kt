package com.javernaut.whatthecodec.feature.settings.di

import com.javernaut.whatthecodec.feature.settings.content.ContentSettingsRepository
import com.javernaut.whatthecodec.feature.settings.impl.content.DefaultContentSettingsRepository
import com.javernaut.whatthecodec.feature.settings.impl.theme.DefaultThemeSettingsRepository
import com.javernaut.whatthecodec.feature.settings.theme.ThemeSettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsModule {

    @Binds
    internal abstract fun bindThemeSettingsRepository(
        impl: DefaultThemeSettingsRepository
    ): ThemeSettingsRepository

    @Binds
    internal abstract fun bindContentSettingsRepository(
        impl: DefaultContentSettingsRepository
    ): ContentSettingsRepository
}
