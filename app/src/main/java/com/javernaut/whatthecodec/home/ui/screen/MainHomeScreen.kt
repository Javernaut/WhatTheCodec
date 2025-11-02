package com.javernaut.whatthecodec.home.ui.screen

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LeadingIconTab
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.compose.common.OrientationLayout
import com.javernaut.whatthecodec.home.presentation.model.AvailableTab
import com.javernaut.whatthecodec.home.presentation.model.ScreenMessage
import com.javernaut.whatthecodec.home.presentation.model.ScreenState
import com.javernaut.whatthecodec.home.ui.audio.AudioPage
import com.javernaut.whatthecodec.home.ui.subtitle.SubtitlePage
import com.javernaut.whatthecodec.home.ui.video.VideoPage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import com.javernaut.whatthecodec.feature.settings.ui.R as RSettings

@Composable
fun MainHomeScreen(
    screenState: ScreenState,
    onVideoIconClick: () -> Unit,
    onAudioIconClick: () -> Unit,
    onSettingsClicked: () -> Unit,
    onCopyValue: (String) -> Unit,
    screenMessage: Flow<ScreenMessage>
) {
    OrientationLayout(
        portraitContent = {
            PortraitMainScreen(
                screenState,
                onVideoIconClick,
                onAudioIconClick,
                onSettingsClicked,
                onCopyValue,
                screenMessage
            )
        },
        landscapeContent = {
            LandscapeMainScreen(
                screenState,
                onVideoIconClick,
                onAudioIconClick,
                onSettingsClicked,
                onCopyValue,
                screenMessage
            )
        }
    )
}

@Composable
fun LandscapeMainScreen(
    screenState: ScreenState,
    onVideoIconClick: () -> Unit,
    onAudioIconClick: () -> Unit,
    onSettingsClicked: () -> Unit,
    onCopyValue: (String) -> Unit,
    screenMessage: Flow<ScreenMessage>
) {
    val tabsToShow = screenState.availableTabs
    val pagerState = rememberPagerState {
        tabsToShow.size
    }

    Row {
        MainScreenSideBar(
            onVideoIconClick, onAudioIconClick, onSettingsClicked
        )

        val snackbarHostState = remember { SnackbarHostState() }

        ObserveScreenMessages(
            screenMassages = screenMessage,
            snackbarHostState = snackbarHostState
        )

        Scaffold(
            modifier = Modifier.consumeWindowInsets(
                WindowInsets.systemBars.only(WindowInsetsSides.Start)
            ),
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            topBar = {
                MainScreenTopAppBar(
                    tabsToShow = tabsToShow,
                    pagerState = pagerState,
                    inPortrait = false
                )
            }
        ) {
            MainScreenContent(
                screenState = screenState,
                pagerState = pagerState,
                contentPadding = it,
                onCopyValue = onCopyValue,
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }
}

@Composable
private fun MainScreenSideBar(
    onVideoIconClick: () -> Unit,
    onAudioIconClick: () -> Unit,
    onSettingsClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .windowInsetsPadding(
                    WindowInsets.systemBars.only(
                        WindowInsetsSides.Vertical + WindowInsetsSides.Start
                    )
                )
                .padding(16.dp),
            verticalArrangement = spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SideMainAction(
                image = Icons.Filled.Videocam,
                contentDescription = stringResource(id = R.string.menu_pick_video),
                clickListener = onVideoIconClick
            )
            SideMainAction(
                image = Icons.Filled.MusicNote,
                contentDescription = stringResource(id = R.string.menu_pick_audio),
                clickListener = onAudioIconClick
            )
            Spacer(modifier = Modifier.weight(1f))
            HomeScreenSettingsAction(onSettingsClicked)
        }
    }
}

@Composable
fun PortraitMainScreen(
    screenState: ScreenState,
    onVideoIconClick: () -> Unit,
    onAudioIconClick: () -> Unit,
    onSettingsClicked: () -> Unit,
    onCopyValue: (String) -> Unit,
    screenMessage: Flow<ScreenMessage>
) {
    val tabsToShow = screenState.availableTabs
    val pagerState = rememberPagerState {
        tabsToShow.size
    }

    val snackbarHostState = remember { SnackbarHostState() }

    ObserveScreenMessages(
        screenMassages = screenMessage,
        snackbarHostState = snackbarHostState
    )

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = {
            MainScreenBottomAppBar(onSettingsClicked, onVideoIconClick, onAudioIconClick)
        },
        topBar = {
            MainScreenTopAppBar(
                tabsToShow = tabsToShow,
                pagerState = pagerState,
                inPortrait = true
            )
        }
    ) {
        MainScreenContent(
            screenState = screenState,
            pagerState = pagerState,
            contentPadding = it,
            onCopyValue = onCopyValue,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun MainScreenTopAppBar(
    tabsToShow: List<AvailableTab>,
    pagerState: PagerState,
    inPortrait: Boolean,
) {
    val scope = rememberCoroutineScope()
    PrimaryTabRow(
        selectedTabIndex = pagerState.currentPage,
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(
                TopAppBarDefaults.windowInsets.only(WindowInsetsSides.Horizontal)
            )
    ) {
        tabsToShow.forEachIndexed { index, tabToShow ->
            MainScreenIconTab(
                tall = inPortrait,
                text = { Text(stringResource(id = tabToShow.title)) },
                icon = {
                    Icon(imageVector = tabToShow.icon, contentDescription = null)
                },
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                modifier = Modifier.windowInsetsPadding(TopAppBarDefaults.windowInsets)
            )
        }
    }
}

@Composable
fun MainScreenIconTab(
    tall: Boolean,
    selected: Boolean,
    onClick: () -> Unit,
    text: @Composable () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (tall) {
        Tab(
            selected = selected,
            onClick = onClick,
            text = text,
            icon = icon,
            modifier = modifier
        )
    } else {
        LeadingIconTab(
            selected = selected,
            onClick = onClick,
            text = text,
            icon = icon,
            modifier = modifier
        )
    }
}

@Composable
private fun MainScreenContent(
    screenState: ScreenState,
    pagerState: PagerState,
    contentPadding: PaddingValues,
    onCopyValue: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pageModifier = Modifier.fillMaxSize()
    HorizontalPager(pagerState, modifier) { page ->
        when (screenState.availableTabs[page]) {
            AvailableTab.VIDEO ->
                VideoPage(screenState.videoPage!!, contentPadding, onCopyValue, pageModifier)

            AvailableTab.AUDIO ->
                AudioPage(screenState.audioPage!!, contentPadding, onCopyValue, pageModifier)

            AvailableTab.SUBTITLES -> SubtitlePage(
                screenState.subtitlesPage!!,
                contentPadding,
                onCopyValue,
                pageModifier
            )
        }
    }
}

@Composable
private fun MainScreenBottomAppBar(
    onSettingsClicked: () -> Unit,
    onVideoIconClick: () -> Unit,
    onAudioIconClick: () -> Unit
) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        actions = {
            HomeScreenSettingsAction(onSettingsClicked)
        },
        floatingActionButton = {
            Row(
                horizontalArrangement = spacedBy(16.dp)
            ) {
                BottomMainAction(
                    image = Icons.Filled.Videocam,
                    contentDescription = stringResource(id = R.string.menu_pick_video),
                    clickListener = onVideoIconClick
                )
                BottomMainAction(
                    image = Icons.Filled.MusicNote,
                    contentDescription = stringResource(id = R.string.menu_pick_audio),
                    clickListener = onAudioIconClick
                )
            }
        }
    )
}

@Composable
private fun BottomMainAction(
    image: ImageVector,
    contentDescription: String,
    clickListener: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = clickListener,
        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
        modifier = modifier
    ) {
        Icon(image, contentDescription)
    }
}

@Composable
private fun SideMainAction(
    image: ImageVector,
    contentDescription: String,
    clickListener: () -> Unit,
    modifier: Modifier = Modifier
) {
    SmallFloatingActionButton(
        onClick = clickListener,
        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
        modifier = modifier
    ) {
        Icon(image, contentDescription)
    }
}

private val AvailableTab.title: Int
    get() = when (this) {
        AvailableTab.VIDEO -> RSettings.string.tab_video
        AvailableTab.AUDIO -> RSettings.string.tab_audio
        AvailableTab.SUBTITLES -> RSettings.string.tab_subtitles
    }

private val AvailableTab.icon: ImageVector
    get() = when (this) {
        AvailableTab.VIDEO -> Icons.Filled.Videocam
        AvailableTab.AUDIO -> Icons.Filled.Audiotrack
        AvailableTab.SUBTITLES -> Icons.Filled.Subtitles
    }
