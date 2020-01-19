package com.javernaut.whatthecodec.presentation.video.ui

import android.os.Bundle
import androidx.lifecycle.Observer
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.audio.ui.BitRateHelper
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.BasicVideoInfo
import com.javernaut.whatthecodec.presentation.stream.BasePageFragment
import com.javernaut.whatthecodec.presentation.stream.model.StreamCard
import com.javernaut.whatthecodec.presentation.stream.model.StreamFeature
import com.javernaut.whatthecodec.presentation.stream.model.makeStream
import kotlinx.android.synthetic.main.fragment_video_page.*

class VideoPageFragment : BasePageFragment(R.layout.fragment_video_page) {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mediaFileViewModel.basicVideoInfoLiveData.observe(this, Observer {
            if (it != null) {
                displayStreams(listOf(
                        convertToContainer(it),
                        convertToStream(it)
                ))
            }
        })

        mediaFileViewModel.previewLiveData.observe(this, Observer {
            previewView.setPreview(it)
        })
    }

    private fun convertToStream(basicVideoInfo: BasicVideoInfo): StreamCard {
        val videoStream = basicVideoInfo.videoStream

        return makeStream(videoStream.basicInfo, resources) {
            add(StreamFeature(R.string.page_video_codec_name, videoStream.basicInfo.codecName))
            if (videoStream.bitRate > 0) {
                add(StreamFeature(R.string.page_video_bit_rate, BitRateHelper.toString(videoStream.bitRate, resources)))
            }

            add(StreamFeature(R.string.page_video_frame_width, videoStream.frameWidth.toString()))
            add(StreamFeature(R.string.page_video_frame_height, videoStream.frameHeight.toString()))
        }
    }

    private fun convertToContainer(basicVideoInfo: BasicVideoInfo): StreamCard {
        return StreamCard(
                getString(R.string.info_container),
                listOf(
                        StreamFeature(R.string.info_file_format, basicVideoInfo.fileFormat),

                        StreamFeature(R.string.info_protocol_title, getString(
                                if (basicVideoInfo.fullFeatured) {
                                    R.string.info_protocol_file
                                } else {
                                    R.string.info_protocol_pipe
                                }))

                )
        )
    }
}