package com.javernaut.whatthecodec.presentation.stream

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.root.viewmodel.MediaFileViewModel
import com.javernaut.whatthecodec.presentation.stream.adapter.StreamsAdapter
import com.javernaut.whatthecodec.presentation.stream.model.StreamCard
import kotlinx.android.synthetic.main.fragment_base_page.*

abstract class BasePageFragment(@LayoutRes layoutId: Int = R.layout.fragment_base_page) : Fragment(layoutId) {

    protected val mediaFileViewModel by activityViewModels<MediaFileViewModel>()

    private val adapter = StreamsAdapter()

    @CallSuper
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    protected fun displayStreams(streamCards: List<StreamCard>) {
        adapter.streamCards = streamCards
    }
}