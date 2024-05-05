package com.javernaut.whatthecodec.screenshots

import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.rule.GrantPermissionRule
import com.javernaut.whatthecodec.compose.theme.WhatTheCodecTheme
import com.javernaut.whatthecodec.compose.theme.dynamic.AppTheme
import com.javernaut.whatthecodec.compose.theme.dynamic.ThemeViewModel
import com.javernaut.whatthecodec.home.data.completeEnumSet
import com.javernaut.whatthecodec.home.data.model.AudioStreamFeature
import com.javernaut.whatthecodec.home.data.model.SubtitleStreamFeature
import com.javernaut.whatthecodec.home.data.model.VideoStreamFeature
import com.javernaut.whatthecodec.home.ui.screen.EmptyHomeScreen
import com.javernaut.whatthecodec.settings.presentation.SettingsViewModel
import com.javernaut.whatthecodec.settings.ui.SettingsScreen
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import tools.fastlane.screengrab.Screengrab
import tools.fastlane.screengrab.locale.LocaleTestRule

@RunWith(Parameterized::class)
class ScreenshotsTestSuite(
    private val darkMode: Boolean
) {
    companion object {
        @JvmStatic
        @Parameters
        fun parameters() = listOf(true, false)
    }

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @get:Rule
    val localeTestRule = LocaleTestRule()

    // TODO make sure the necessary permissions are given to all supported API levels
    @get:Rule
    val mRuntimePermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant("android.permission.DUMP")

    // TODO Use 3 button navigation for dark mode and gesture navigation for light mode
    // TODO Use the Demo Mode (status bar clearing - no notifications)
    // TODO Status bar should have proper color
    // TODO Tests should run on 2 specific devices and screenshots have to be grabbed from all of them
    @Test
    fun emptyScreen() {
        makeScreenshotOf("empty") {
            EmptyHomeScreen(
                onVideoIconClick = { },
                onAudioIconClick = { },
                onSettingsClicked = { },
                screenMassages = emptyFlow()
            )
        }
    }

    @Test
    fun settingsScreen() {
        val appThemeFlow = MutableStateFlow(AppTheme.Auto)
        val themeViewModel = mockk<ThemeViewModel>()
        every { themeViewModel.appTheme } returns appThemeFlow

        val settingsViewModel = mockk<SettingsViewModel>()

        val videoFeatures = MutableStateFlow(completeEnumSet<VideoStreamFeature>())
        val audioFeatures = MutableStateFlow(completeEnumSet<AudioStreamFeature>())
        val subtitleFeatures = MutableStateFlow(completeEnumSet<SubtitleStreamFeature>())

        every { settingsViewModel.audioStreamFeatures } returns audioFeatures
        every { settingsViewModel.videoStreamFeatures } returns videoFeatures
        every { settingsViewModel.subtitleStreamFeatures } returns subtitleFeatures

        makeScreenshotOf("settings") {
            SettingsScreen(themeViewModel, settingsViewModel, {}, {})
        }
    }

    private fun makeScreenshotOf(name: String, content: @Composable () -> Unit) {
        composeTestRule.activityRule.scenario.onActivity {
            it.enableEdgeToEdge()
        }
        composeTestRule.setContent {
            WhatTheCodecTheme.Static(darkTheme = darkMode, content = content)
        }
        // Waiting for Compose to fully render +
        // waiting for Navigation and Status bars to adjust their color.
        // transition_animation_scale, window_animation_scale and animator_duration_scale doesn't
        // seem to have any impact on the latter.
        Thread.sleep(100)

        val suffix = if (darkMode) "dark" else "light"
        Screengrab.screenshot("${name}_${suffix}")
    }
}
