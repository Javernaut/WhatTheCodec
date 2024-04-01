package com.javernaut.whatthecodec.presentation.root.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.audio.AudioPage
import com.javernaut.whatthecodec.presentation.root.viewmodel.ScreenState
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.AvailableTab
import com.javernaut.whatthecodec.presentation.subtitle.SubtitlePage
import com.javernaut.whatthecodec.presentation.video.VideoPage
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
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
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MainScreenTopAppBar(
                tabsToShow = tabsToShow,
                pagerState,
                scrollBehavior,
                onVideoIconClick,
                onAudioIconClick,
                onSettingsClicked
            )
        }
    ) {
        MainScreenContent(tabsToShow, screenState, pagerState, it, Modifier.fillMaxSize())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@ExperimentalFoundationApi
private fun MainScreenTopAppBar(
    tabsToShow: List<AvailableTab>,
    pagerState: PagerState,
    scrollBehavior: TopAppBarScrollBehavior,
    onVideoIconClick: () -> Unit,
    onAudioIconClick: () -> Unit,
    onSettingsClicked: () -> Unit
) {
    TopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            val scope = rememberCoroutineScope()
            ScrollableTabRow(
                edgePadding = 0.dp,
                containerColor = Color.Transparent,
                // Our selected tab is our current page
                selectedTabIndex = pagerState.currentPage
            ) {
                // Add tabs for all of our pages
                tabsToShow.forEachIndexed { index, tabToShow ->
                    Tab(
                        // Hack. Setting the Tab itself to be as tall as the content area of a small
                        // top app bar. Modifier.fillMaxHeight() doesn't work.
                        modifier = Modifier.height(64.dp),
                        text = { Text(stringResource(id = tabToShow.title)) },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = onVideoIconClick) {
                Icon(
                    Icons.Filled.Videocam,
                    contentDescription = stringResource(id = R.string.menu_pick_video),
                )
            }
            IconButton(onClick = onAudioIconClick) {
                Icon(
                    Icons.Filled.MusicNote,
                    contentDescription = stringResource(id = R.string.menu_pick_audio),
                )
            }
            IconButton(onClick = onSettingsClicked) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = stringResource(id = R.string.menu_settings),
                )
            }
        }
    )
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
    HorizontalPager(pagerState, modifier) { page ->
        val pageModifier = Modifier.fillMaxSize()
        when (tabsToShow[page]) {
            AvailableTab.VIDEO -> VideoPage(screenState.videoPage!!, contentPadding, pageModifier)
            AvailableTab.AUDIO -> AudioPage(screenState.audioPage!!, contentPadding, pageModifier)
            AvailableTab.SUBTITLES -> SubtitlePage(screenState.subtitlesPage!!, contentPadding, pageModifier)
        }
    }
}
