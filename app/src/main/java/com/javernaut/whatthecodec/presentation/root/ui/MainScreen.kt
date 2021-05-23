package com.javernaut.whatthecodec.presentation.root.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.*
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.audio.ui.AudioPage
import com.javernaut.whatthecodec.presentation.compose.common.getAppBarElevation
import com.javernaut.whatthecodec.presentation.root.viewmodel.MediaFileViewModel
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.AvailableTab
import com.javernaut.whatthecodec.presentation.subtitle.ui.SubtitlePage
import com.javernaut.whatthecodec.presentation.video.ui.VideoPage
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalPagerApi::class)
fun MainScreen(
    tabsToShow: List<AvailableTab>,
    mediaFileViewModel: MediaFileViewModel,
    menuActions: @Composable RowScope.() -> Unit = {}
) {
    val pagerState = rememberPagerState(pageCount = tabsToShow.size)
    Scaffold(
        topBar = {
            MainScreenTopAppBar(tabsToShow = tabsToShow, pagerState, menuActions)
        }
    ) {
        MainScreenContent(Modifier.padding(it), tabsToShow, pagerState, mediaFileViewModel)
    }
}


@Composable
@ExperimentalPagerApi
private fun MainScreenTopAppBar(
    tabsToShow: List<AvailableTab>,
    pagerState: PagerState,
    menuActions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        elevation = getAppBarElevation(),
        title = {
            val scope = rememberCoroutineScope()
            ScrollableTabRow(
                edgePadding = 0.dp,
                // Our selected tab is our current page
                selectedTabIndex = pagerState.currentPage,
                // Override the indicator, using the provided pagerTabIndicatorOffset modifier
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                    )
                }
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
@ExperimentalPagerApi
private fun MainScreenContent(
    modifier: Modifier = Modifier,
    tabsToShow: List<AvailableTab>,
    pagerState: PagerState,
    viewModel: MediaFileViewModel
) {
    HorizontalPager(pagerState, modifier) { page ->
        when (tabsToShow[page]) {
            AvailableTab.VIDEO -> VideoPage(viewModel)
            AvailableTab.AUDIO -> AudioPage(viewModel)
            AvailableTab.SUBTITLES -> SubtitlePage(viewModel)
        }
    }
}