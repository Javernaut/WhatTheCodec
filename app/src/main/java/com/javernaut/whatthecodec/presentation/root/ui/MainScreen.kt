package com.javernaut.whatthecodec.presentation.root.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Scaffold
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.Text
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
    menuActions: @Composable RowScope.() -> Unit = {}
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
            MainScreenTopAppBar(tabsToShow = tabsToShow, pagerState, menuActions)
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
    menuActions: @Composable RowScope.() -> Unit = {}
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
        actions = menuActions
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
