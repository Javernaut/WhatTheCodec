package com.javernaut.whatthecodec.presentation.stream

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.compose.theme.WhatTheCodecTheme
import com.javernaut.whatthecodec.presentation.root.viewmodel.MediaFileViewModel
import com.javernaut.whatthecodec.presentation.stream.adapter.StreamCard
import com.javernaut.whatthecodec.presentation.stream.model.StreamCard

abstract class BasePageFragment : Fragment(R.layout.fragment_base_page) {

    protected val mediaFileViewModel by activityViewModels<MediaFileViewModel>()

    protected lateinit var composeView: ComposeView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        composeView = view.findViewById(R.id.composeView)
    }

    protected fun displayStreams(streamCards: List<StreamCard>) {
        composeView.setContent {
            WhatTheCodecTheme {
                LazyColumn(
                    Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp)
                ) {
                    StreamCardsInColumn(streamCards)
                }
            }
        }
    }

    protected fun LazyListScope.StreamCardsInColumn(streamCards: List<StreamCard>) {
        val commonModifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        items(streamCards) {
            StreamCard(commonModifier, it)
        }
    }
}

