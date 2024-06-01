package com.javernaut.whatthecodec.feature.settings.di

import com.javernaut.whatthecodec.feature.settings.api.content.ContentSettingsRepository
import com.javernaut.whatthecodec.feature.settings.api.theme.ThemeSettingsRepository
import com.javernaut.whatthecodec.feature.settings.data.content.DefaultContentSettingsRepository
import com.javernaut.whatthecodec.feature.settings.data.theme.DefaultThemeSettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsDataModule {

    @Binds
    internal abstract fun bindThemeSettingsRepository(
        impl: DefaultThemeSettingsRepository
    ): ThemeSettingsRepository

    @Binds
    internal abstract fun bindContentSettingsRepository(
        impl: DefaultContentSettingsRepository
    ): ContentSettingsRepository
}
