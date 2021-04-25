package com.javernaut.whatthecodec.presentation.compose.playground

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.google.accompanist.pager.*
import com.javernaut.whatthecodec.presentation.compose.theme.WhatTheCodecTheme
import kotlinx.coroutines.launch

@ExperimentalPagerApi
class ComposePlaygroundActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pages = listOf("One", "Two", "Three")
        setContent {
            WhatTheCodecTheme {
                val pagerState = rememberPagerState(pageCount = pages.size)
                Scaffold(
                    topBar = {
                        TopAppBar {
                            Tabs(pages, pagerState)
                        }
                    }
                ) {
                    Pager(pages, pagerState)
                }
            }
        }
    }

    @Composable
    private fun Tabs(pages: List<String>, pagerState: PagerState) {
        val scope = rememberCoroutineScope()
        TabRow(
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
            pages.forEachIndexed { index, title ->
                Tab(
                    modifier = Modifier.fillMaxHeight(),
                    text = { Text(title) },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                )
            }
        }
    }

    @Composable
    private fun Pager(pages: List<String>, pagerState: PagerState) {
        HorizontalPager(state = pagerState) { page ->
            Text(
                modifier = Modifier.fillMaxSize(),
                text = pages[page]
            )
        }
    }
}