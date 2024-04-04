package com.javernaut.whatthecodec.home.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LeadingIconTab
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.compose.common.OrientationLayout
import com.javernaut.whatthecodec.home.presentation.ScreenState
import com.javernaut.whatthecodec.home.presentation.model.AvailableTab
import com.javernaut.whatthecodec.home.ui.audio.AudioPage
import com.javernaut.whatthecodec.home.ui.subtitle.SubtitlePage
import com.javernaut.whatthecodec.home.ui.video.VideoPage
import kotlinx.coroutines.launch

@Composable
fun MainHomeScreen(
    screenState: ScreenState,
    onVideoIconClick: () -> Unit,
    onAudioIconClick: () -> Unit,
    onSettingsClicked: () -> Unit
) {
    OrientationLayout(
        portraitContent = {
            PortraitMainScreen(
                screenState,
                onVideoIconClick,
                onAudioIconClick,
                onSettingsClicked
            )
        },
        landscapeContent = {
            LandscapeMainScreen(
                screenState,
                onVideoIconClick,
                onAudioIconClick,
                onSettingsClicked
            )
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LandscapeMainScreen(
    screenState: ScreenState,
    onVideoIconClick: () -> Unit,
    onAudioIconClick: () -> Unit,
    onSettingsClicked: () -> Unit
) {
    val tabsToShow = screenState.availableTabs
    val pagerState = rememberPagerState {
        tabsToShow.size
    }

    Row {
        MainScreenSideBar(
            onVideoIconClick, onAudioIconClick, onSettingsClicked
        )
        Scaffold(
            modifier = Modifier.consumeWindowInsets(
                WindowInsets.systemBars.only(WindowInsetsSides.Start)
            ),
            topBar = {
                MainScreenTopAppBar(
                    tabsToShow = tabsToShow,
                    pagerState = pagerState,
                )
            }
        ) {
            MainScreenContent(
                screenState = screenState,
                pagerState = pagerState,
                contentPadding = it,
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
@OptIn(ExperimentalFoundationApi::class)
fun PortraitMainScreen(
    screenState: ScreenState,
    onVideoIconClick: () -> Unit,
    onAudioIconClick: () -> Unit,
    onSettingsClicked: () -> Unit
) {
    val tabsToShow = screenState.availableTabs
    val pagerState = rememberPagerState {
        tabsToShow.size
    }

    Scaffold(
        bottomBar = {
            MainScreenBottomAppBar(onSettingsClicked, onVideoIconClick, onAudioIconClick)
        },
        topBar = {
            MainScreenTopAppBar(
                tabsToShow = tabsToShow,
                pagerState = pagerState,
            )
        }
    ) {
        MainScreenContent(
            screenState = screenState,
            pagerState = pagerState,
            contentPadding = it,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@ExperimentalFoundationApi
private fun MainScreenTopAppBar(
    tabsToShow: List<AvailableTab>,
    pagerState: PagerState,
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
            LeadingIconTab(
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
@ExperimentalFoundationApi
private fun MainScreenContent(
    screenState: ScreenState,
    pagerState: PagerState,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val pageModifier = Modifier.fillMaxSize()
    HorizontalPager(pagerState, modifier) { page ->
        when (screenState.availableTabs[page]) {
            AvailableTab.VIDEO ->
                VideoPage(screenState.videoPage!!, contentPadding, pageModifier)

            AvailableTab.AUDIO ->
                AudioPage(screenState.audioPage!!, contentPadding, pageModifier)

            AvailableTab.SUBTITLES -> SubtitlePage(
                screenState.subtitlesPage!!,
                contentPadding,
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
        AvailableTab.VIDEO -> R.string.tab_video
        AvailableTab.AUDIO -> R.string.tab_audio
        AvailableTab.SUBTITLES -> R.string.tab_subtitles
    }

private val AvailableTab.icon: ImageVector
    get() = when (this) {
        AvailableTab.VIDEO -> Icons.Filled.Videocam
        AvailableTab.AUDIO -> Icons.Filled.Audiotrack
        AvailableTab.SUBTITLES -> Icons.Filled.Subtitles
    }
