package com.javernaut.whatthecodec.presentation.audio.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.root.viewmodel.VideoInfoViewModel
import kotlinx.android.synthetic.main.fragment_audio_page.*

class AudioPageFragment : Fragment(R.layout.fragment_audio_page) {

    private val videoInfoViewModel by activityViewModels<VideoInfoViewModel>()

    private val adapter = AudioStreamsAdapter()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        videoInfoViewModel.audioStreamsLiveData.observe(this, Observer {
            adapter.streams = it
        })
    }
}