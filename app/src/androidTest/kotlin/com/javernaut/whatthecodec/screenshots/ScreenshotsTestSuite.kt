package com.javernaut.whatthecodec.screenshots

import android.graphics.BitmapFactory
import android.os.Build
import android.util.Size
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.javernaut.whatthecodec.compose.theme.WhatTheCodecTheme
import com.javernaut.whatthecodec.feature.settings.api.content.AudioStreamFeature
import com.javernaut.whatthecodec.feature.settings.api.content.ContentSettingsRepository
import com.javernaut.whatthecodec.feature.settings.api.content.SubtitleStreamFeature
import com.javernaut.whatthecodec.feature.settings.api.content.VideoStreamFeature
import com.javernaut.whatthecodec.feature.settings.api.theme.AppTheme
import com.javernaut.whatthecodec.feature.settings.api.theme.ThemeSettings
import com.javernaut.whatthecodec.feature.settings.api.theme.ThemeSettingsRepository
import com.javernaut.whatthecodec.feature.settings.data.content.completeEnumSet
import com.javernaut.whatthecodec.feature.settings.presentation.SettingsViewModel
import com.javernaut.whatthecodec.feature.settings.ui.SettingsScreen
import com.javernaut.whatthecodec.home.presentation.model.ActualFrame
import com.javernaut.whatthecodec.home.presentation.model.ActualPreview
import com.javernaut.whatthecodec.home.presentation.model.AudioPage
import com.javernaut.whatthecodec.home.presentation.model.ScreenState
import com.javernaut.whatthecodec.home.presentation.model.SubtitlesPage
import com.javernaut.whatthecodec.home.presentation.model.VideoPage
import com.javernaut.whatthecodec.home.ui.screen.EmptyHomeScreen
import com.javernaut.whatthecodec.home.ui.screen.MainHomeScreen
import io.github.javernaut.mediafile.model.AudioStream
import io.github.javernaut.mediafile.model.BasicStreamInfo
import io.github.javernaut.mediafile.model.BitRate
import io.github.javernaut.mediafile.model.Container
import io.github.javernaut.mediafile.model.FrameRate
import io.github.javernaut.mediafile.model.SampleRate
import io.github.javernaut.mediafile.model.VideoStream
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
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
        val videoStream = VideoStream(
            BasicStreamInfo(
                index = 0,
                title = null,
                codecName = "H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10",
                language = null,
                disposition = 0
            ),
            bitRate = BitRate(4_500_000),
            frameRate = FrameRate(25.0),
            frameSize = Size(1840, 1034)
        )

        val assets = InstrumentationRegistry.getInstrumentation().context.assets
        val preview = ActualPreview(
            frameMetrics = Size(720, 405),
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
                Container("QuickTime / MOV"),
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
        val audioStream = AudioStream(
            BasicStreamInfo(
                index = 0,
                title = null,
                codecName = "MP3 (MPEG audio layer 3)",
                language = null,
                disposition = 0
            ),
            bitRate = BitRate(320_000),
            sampleFormat = "fltp",
            sampleRate = SampleRate(44_100),
            channels = 2,
            channelLayout = "stereo",
        )

        val screenState = ScreenState(
            videoPage = null,
            audioPage = AudioPage(
                streams = listOf(audioStream),
                streamFeatures = completeEnumSet<AudioStreamFeature>()
            ),
            subtitlesPage = null
        )
        makeScreenshotOf("audio") {
            MainHomeScreen(screenState, {}, {}, {}, {}, emptyFlow())
        }
    }

    @Test
    fun settingsScreen() {
        val contentSettingsRepository = object : ContentSettingsRepository {
            override val videoStreamFeatures: Flow<Set<VideoStreamFeature>>
                get() = flowOf(completeEnumSet())
            override val audioStreamFeatures: Flow<Set<AudioStreamFeature>>
                get() = flowOf(completeEnumSet())
            override val subtitleStreamFeatures: Flow<Set<SubtitleStreamFeature>>
                get() = flowOf(completeEnumSet())

            override suspend fun setVideoStreamFeatures(newVideoStreamFeatures: Set<VideoStreamFeature>) =
                Unit

            override suspend fun setAudioStreamFeatures(newAudioStreamFeatures: Set<AudioStreamFeature>) =
                Unit

            override suspend fun setSubtitleStreamFeatures(newSubtitleStreamFeatures: Set<SubtitleStreamFeature>) =
                Unit
        }

        val themeSettingsRepository = object : ThemeSettingsRepository {
            override val themeSettings: Flow<ThemeSettings>
                get() = flowOf(ThemeSettings(AppTheme.Auto))

            override suspend fun setSelectedTheme(newAppTheme: AppTheme) = Unit
        }

        makeScreenshotOf("settings") {
            SettingsScreen(
                SettingsViewModel(contentSettingsRepository, themeSettingsRepository),
                {},
                {}
            )
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
