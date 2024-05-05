package com.javernaut.whatthecodec.screenshots

import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.javernaut.whatthecodec.compose.theme.WhatTheCodecTheme
import com.javernaut.whatthecodec.home.ui.screen.EmptyHomeScreen
import kotlinx.coroutines.flow.emptyFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameter
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
    // TODO Use the Demo Mode (status bar clearing)
    // TODO Status bar should have proper color
    // TODO Tests should run on 2 specific devices and screenshots have to be grabbed from all of them
    @Test
    fun emptyScreen() {
        composeTestRule.activityRule.scenario.onActivity {
            it.enableEdgeToEdge()
        }
        composeTestRule.setContent {
            WhatTheCodecTheme.Static(darkTheme = darkMode) {
                EmptyHomeScreen(
                    onVideoIconClick = { },
                    onAudioIconClick = { },
                    onSettingsClicked = { },
                    screenMassages = emptyFlow()
                )
            }
        }
        composeTestRule.waitForIdle()

        val suffix = if (darkMode) "dark" else "light"
        Screengrab.screenshot("empty_$suffix")
    }
}
