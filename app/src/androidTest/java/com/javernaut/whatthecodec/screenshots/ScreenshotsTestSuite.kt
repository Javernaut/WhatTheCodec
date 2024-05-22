package com.javernaut.whatthecodec.screenshots

import android.graphics.BitmapFactory
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.javernaut.whatthecodec.compose.theme.WhatTheCodecTheme
import com.javernaut.whatthecodec.compose.theme.dynamic.ThemeViewModel
import com.javernaut.whatthecodec.feature.settings.theme.AppTheme
import com.javernaut.whatthecodec.home.data.completeEnumSet
import com.javernaut.whatthecodec.home.data.model.AudioStreamFeature
import com.javernaut.whatthecodec.home.data.model.SubtitleStreamFeature
import com.javernaut.whatthecodec.home.data.model.VideoStreamFeature
import com.javernaut.whatthecodec.home.presentation.model.ActualFrame
import com.javernaut.whatthecodec.home.presentation.model.ActualPreview
import com.javernaut.whatthecodec.home.presentation.model.AudioPage
import com.javernaut.whatthecodec.home.presentation.model.FrameMetrics
import com.javernaut.whatthecodec.home.presentation.model.ScreenState
import com.javernaut.whatthecodec.home.presentation.model.SubtitlesPage
import com.javernaut.whatthecodec.home.presentation.model.VideoPage
import com.javernaut.whatthecodec.home.ui.screen.EmptyHomeScreen
import com.javernaut.whatthecodec.home.ui.screen.MainHomeScreen
import com.javernaut.whatthecodec.settings.presentation.SettingsViewModel
import com.javernaut.whatthecodec.settings.ui.SettingsScreen
import io.github.javernaut.mediafile.AudioStream
import io.github.javernaut.mediafile.BasicStreamInfo
import io.github.javernaut.mediafile.VideoStream
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import org.junit.After
import org.junit.Assume
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import tools.fastlane.screengrab.Screengrab
import tools.fastlane.screengrab.cleanstatusbar.BluetoothState
import tools.fastlane.screengrab.cleanstatusbar.CleanStatusBar
import tools.fastlane.screengrab.cleanstatusbar.IconVisibility
import tools.fastlane.screengrab.cleanstatusbar.MobileDataType
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

    init {
        val isFastlane = InstrumentationRegistry.getArguments().getString("fastlane-screenshots")
        Assume.assumeTrue(
            "This test suite should run only with 'fastlane screenshots'",
            "true" == isFastlane
        )

        resetNavigationMethod()
    }

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @get:Rule
    val localeTestRule = LocaleTestRule()

    private val uiDevice
        get() = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    @Before
    fun setUp() {
        CleanStatusBar()
            .setBluetoothState(BluetoothState.DISCONNECTED)
            .setMobileNetworkDataType(MobileDataType.LTE)
            .setWifiVisibility(IconVisibility.HIDE)
            .setShowNotifications(false)
            .setClock("0900")
            .setBatteryLevel(100)
            .enable()
    }

    @After
    fun tearDown() {
        CleanStatusBar.disable()
    }

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
    fun videoTabScreen() {
        val basicStreamInfo = mockk<BasicStreamInfo>()
        every { basicStreamInfo.index } returns 0
        every { basicStreamInfo.title } returns null
        every { basicStreamInfo.codecName } returns "H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10"
        every { basicStreamInfo.language } returns null
        every { basicStreamInfo.disposition } returns 0

        val videoStream = mockk<VideoStream>()
        every { videoStream.basicInfo } returns basicStreamInfo
        every { videoStream.frameHeight } returns 1034
        every { videoStream.frameWidth } returns 1840
        every { videoStream.frameRate } returns 25.0
        every { videoStream.bitRate } returns 4_500_000

        val assets = InstrumentationRegistry.getInstrumentation().context.assets
        val preview = ActualPreview(
            frameMetrics = FrameMetrics(720, 405),
            frames = (1..4).map {
                val inputStream = assets.open("video_frame_$it.png")
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()
                ActualFrame(bitmap)
            },
            backgroundColor = (0xFF785050).toInt()
        )

        val screenState = ScreenState(
            videoPage = VideoPage(
                preview,
                "QuickTime / MOV",
                true,
                videoStream,
                videoStreamFeatures = completeEnumSet()
            ),
            // These 2 are visible only as tabs
            audioPage = AudioPage(streams = emptyList(), streamFeatures = emptySet()),
            subtitlesPage = SubtitlesPage(streams = emptyList(), streamFeatures = emptySet())
        )
        makeScreenshotOf("video") {
            MainHomeScreen(screenState, {}, {}, {}, {}, emptyFlow())
        }
    }


    @Test
    fun audioTabScreen() {
        val basicStreamInfo = mockk<BasicStreamInfo>()
        every { basicStreamInfo.index } returns 0
        every { basicStreamInfo.title } returns null
        every { basicStreamInfo.codecName } returns "MP3 (MPEG audio layer 3)"
        every { basicStreamInfo.language } returns null
        every { basicStreamInfo.disposition } returns 0

        val audioStream = mockk<AudioStream>()
        every { audioStream.basicInfo } returns basicStreamInfo
        every { audioStream.bitRate } returns 320_000
        every { audioStream.sampleFormat } returns "fltp"
        every { audioStream.sampleRate } returns 44_100
        every { audioStream.channels } returns 2
        every { audioStream.channelLayout } returns "stereo"

        val screenState = ScreenState(
            videoPage = null,
            audioPage = AudioPage(
                streams = listOf(audioStream),
                streamFeatures = completeEnumSet()
            ),
            subtitlesPage = null
        )
        makeScreenshotOf("audio") {
            MainHomeScreen(screenState, {}, {}, {}, {}, emptyFlow())
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
        composeTestRule.activityRule.scenario.onActivity(
            ComponentActivity::enableEdgeToEdge
        )

        composeTestRule.setContent {
            WhatTheCodecTheme.Static(darkTheme = darkMode, content = content)
        }

        uiDevice.waitForIdle()

        val suffix = if (darkMode) "dark" else "light"
        Screengrab.screenshot("${name}_${suffix}")
    }

    private fun resetNavigationMethod() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val action = if (darkMode) "disable" else "enable"
            uiDevice
                .executeShellCommand("cmd overlay $action com.android.internal.systemui.navbar.gestural")
        }
    }
}
