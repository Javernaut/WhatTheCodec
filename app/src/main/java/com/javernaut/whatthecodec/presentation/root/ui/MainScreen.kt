package com.javernaut.whatthecodec.presentation.root.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.audio.AudioPage
import com.javernaut.whatthecodec.presentation.compose.common.WtcTopAppBar
import com.javernaut.whatthecodec.presentation.root.viewmodel.ScreenState
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.AvailableTab
import com.javernaut.whatthecodec.presentation.subtitle.SubtitlePage
import com.javernaut.whatthecodec.presentation.video.VideoPage
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
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) {
        tabsToShow.size
    }
    Scaffold(
        topBar = {
            MainScreenTopAppBar(
                tabsToShow = tabsToShow,
                pagerState,
                onVideoIconClick,
                onAudioIconClick,
                onSettingsClicked
            )
        }
    ) {
        MainScreenContent(Modifier.padding(it), tabsToShow, screenState, pagerState)
    }
}

@Composable
@ExperimentalFoundationApi
private fun MainScreenTopAppBar(
    tabsToShow: List<AvailableTab>,
    pagerState: PagerState,
    onVideoIconClick: () -> Unit,
    onAudioIconClick: () -> Unit,
    onSettingsClicked: () -> Unit
) {
    WtcTopAppBar(
        title = {
            val scope = rememberCoroutineScope()
            ScrollableTabRow(
                edgePadding = 0.dp,
                // Our selected tab is our current page
                selectedTabIndex = pagerState.currentPage
            ) {
                // Add tabs for all of our pages
                tabsToShow.forEachIndexed { index, tabToShow ->
                    Tab(
                        modifier = Modifier.fillMaxHeight(),
                        text = { Text(stringResource(id = tabToShow.title).toUpperCase()) },
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
    modifier: Modifier = Modifier,
    tabsToShow: List<AvailableTab>,
    screenState: ScreenState,
    pagerState: PagerState,
) {
    HorizontalPager(pagerState, modifier) { page ->
        val pageModifier = Modifier.fillMaxSize()
        when (tabsToShow[page]) {
            AvailableTab.VIDEO -> VideoPage(screenState.videoPage!!, pageModifier)
            AvailableTab.AUDIO -> AudioPage(screenState.audioPage!!, pageModifier)
            AvailableTab.SUBTITLES -> SubtitlePage(screenState.subtitlesPage!!, pageModifier)
        }
    }
}
