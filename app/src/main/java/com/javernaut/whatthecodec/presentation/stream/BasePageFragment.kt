package com.javernaut.whatthecodec.presentation.stream

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.root.viewmodel.MediaFileViewModel
import com.javernaut.whatthecodec.presentation.stream.adapter.StreamCard
import com.javernaut.whatthecodec.presentation.stream.model.StreamCard

abstract class BasePageFragment(@LayoutRes layoutId: Int = R.layout.fragment_base_page) :
    Fragment(layoutId) {

    protected val mediaFileViewModel by activityViewModels<MediaFileViewModel>()

    private lateinit var composeView: ComposeView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        composeView = view.findViewById(R.id.composeView)
    }

    protected fun displayStreams(streamCards: List<StreamCard>) {
        composeView.setContent {
            StreamCardsColumn(streamCards)
        }
    }
}

@Composable
fun StreamCardsColumn(streamCards: List<StreamCard>) {
    // TODO migrate to LazyColumn
    Column(
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        streamCards.forEach {
            StreamCard(it)
        }
    }
}
