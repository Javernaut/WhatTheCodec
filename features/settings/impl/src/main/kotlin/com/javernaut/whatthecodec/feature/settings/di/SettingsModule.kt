package com.javernaut.whatthecodec.feature.settings.di

import com.javernaut.whatthecodec.feature.settings.impl.DefaultThemeSettingsRepository
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
}
