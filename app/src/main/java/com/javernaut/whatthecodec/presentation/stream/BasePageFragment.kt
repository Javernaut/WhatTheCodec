package com.javernaut.whatthecodec.presentation.stream

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.root.viewmodel.MediaFileViewModel
import com.javernaut.whatthecodec.presentation.stream.adapter.StreamsAdapter
import com.javernaut.whatthecodec.presentation.stream.model.Stream
import kotlinx.android.synthetic.main.fragment_base_page.*

abstract class BasePageFragment : Fragment(R.layout.fragment_base_page) {

    protected val mediaFileViewModel by activityViewModels<MediaFileViewModel>()

    private val adapter = StreamsAdapter()

    final override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        onSubscribeToViewModel()
    }

    protected abstract fun onSubscribeToViewModel()

    protected fun displayStreams(streams: List<Stream>) {
        adapter.streams = streams
    }
}