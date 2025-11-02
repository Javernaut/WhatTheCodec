package com.javernaut.whatthecodec.home.ui.screen

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
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
        val snackbarHostState = remember { SnackbarHostState() }

        ObserveScreenMessages(
            screenMassages = screenMessage,
            snackbarHostState = snackbarHostState
        )

        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            topBar = {
                LandscapeMainScreenTopAppBar(
                    tabsToShow,
                    pagerState,
                    onVideoIconClick,
                    onAudioIconClick,
                    onSettingsClicked,
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
    MainScreenTabRow(
        pagerState = pagerState,
        inPortrait = inPortrait,
        modifier = Modifier.fillMaxWidth()
    ) {
        MainScreenTopAppBarTabs(
            tabsToShow = tabsToShow,
            pagerState = pagerState,
            inPortrait = inPortrait
        ) { index ->
            scope.launch {
                pagerState.animateScrollToPage(index)
            }
        }
    }
}

@Composable
private fun LandscapeMainScreenTopAppBar(
    tabsToShow: List<AvailableTab>,
    pagerState: PagerState,
    onVideoIconClick: () -> Unit,
    onAudioIconClick: () -> Unit,
    onSettingsClicked: () -> Unit,
) {
    Box {
        MainScreenTopAppBar(
            tabsToShow = tabsToShow,
            pagerState = pagerState,
            inPortrait = false
        )
        Row(
            Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .windowInsetsPadding(TopAppBarDefaults.windowInsets)
        ) {
            // TODO Reconsider the styling of these action elements
            // TODO Use tooltips?
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
            HomeScreenSettingsAction(onSettingsClicked)
        }
    }
}

@Composable
private fun MainScreenTabRow(
    pagerState: PagerState,
    inPortrait: Boolean,
    modifier: Modifier = Modifier,
    tabs: @Composable () -> Unit,
) {
    if (inPortrait) {
        PrimaryTabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = modifier,
            tabs = tabs
        )
    } else {
        // TODO edgePadding = 0.dp?
        PrimaryScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = modifier,
            indicator = {
                TabRowDefaults.PrimaryIndicator(
                    Modifier
                        // There is a small glitch of animating the indicator from and to the 0 position.
                        // Check out the tabIndicatorOffset {} version of this method for potential fix.
                        .tabIndicatorOffset(pagerState.currentPage, matchContentSize = true)
                        .then(
                            if (pagerState.currentPage == 0) Modifier.windowInsetsPadding(
                                TopAppBarDefaults.windowInsets.only(
                                    WindowInsetsSides.Start
                                )
                            )
                            else Modifier
                        ),
                    width = Dp.Unspecified,
                )
            },
            tabs = tabs
        )
    }
}

@Composable
private fun MainScreenTopAppBarTabs(
    tabsToShow: List<AvailableTab>,
    pagerState: PagerState,
    inPortrait: Boolean,
    onTabClicked: (Int) -> Unit
) {
    tabsToShow.forEachIndexed { index, tabToShow ->
        var windowInsetsSides = WindowInsetsSides.Top
        if (index == 0) {
            windowInsetsSides += WindowInsetsSides.Start
        }

        MainScreenIconTab(
            tall = inPortrait,
            text = { Text(stringResource(id = tabToShow.title)) },
            icon = {
                Icon(imageVector = tabToShow.icon, contentDescription = null)
            },
            selected = pagerState.currentPage == index,
            onClick = { onTabClicked(index) },
            modifier = Modifier.windowInsetsPadding(
                TopAppBarDefaults.windowInsets.only(windowInsetsSides)
            )
        )
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
