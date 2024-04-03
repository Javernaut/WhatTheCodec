package com.javernaut.whatthecodec.home.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.home.presentation.ScreenState
import com.javernaut.whatthecodec.home.presentation.model.AvailableTab
import com.javernaut.whatthecodec.home.ui.audio.AudioPage
import com.javernaut.whatthecodec.home.ui.subtitle.SubtitlePage
import com.javernaut.whatthecodec.home.ui.video.VideoPage
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun MainScreen(
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
            tabsToShow = tabsToShow,
            screenState = screenState,
            pagerState = pagerState,
            it,
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
                WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)
            )
    ) {
        tabsToShow.forEachIndexed { index, tabToShow ->
            Tab(
                text = { Text(stringResource(id = tabToShow.title)) },
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

private val AvailableTab.title: Int
    get() = when (this) {
        AvailableTab.VIDEO -> R.string.tab_video
        AvailableTab.AUDIO -> R.string.tab_audio
        AvailableTab.SUBTITLES -> R.string.tab_subtitles
    }

@Composable
@ExperimentalFoundationApi
private fun MainScreenContent(
    tabsToShow: List<AvailableTab>,
    screenState: ScreenState,
    pagerState: PagerState,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val pageModifier = Modifier.fillMaxSize()
    HorizontalPager(pagerState, modifier) { page ->
        when (tabsToShow[page]) {
            AvailableTab.VIDEO -> VideoPage(screenState.videoPage!!, contentPadding, pageModifier)
            AvailableTab.AUDIO -> AudioPage(screenState.audioPage!!, contentPadding, pageModifier)
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
        actions = {
            IconButton(onClick = onSettingsClicked) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = stringResource(id = R.string.menu_settings),
                )
            }
        },
        floatingActionButton = {
            Row(
                horizontalArrangement = spacedBy(16.dp)
            ) {
                FloatingActionButton(
                    onClick = onVideoIconClick,
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                ) {
                    Icon(
                        Icons.Filled.Videocam,
                        contentDescription = stringResource(id = R.string.menu_pick_video),
                    )
                }
                FloatingActionButton(
                    onClick = onAudioIconClick,
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                ) {
                    Icon(
                        Icons.Filled.MusicNote,
                        contentDescription = stringResource(id = R.string.menu_pick_audio),
                    )
                }
            }
        }
    )
}
