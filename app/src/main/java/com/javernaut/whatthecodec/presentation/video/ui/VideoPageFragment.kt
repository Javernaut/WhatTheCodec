package com.javernaut.whatthecodec.presentation.video.ui

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.app.Dialog
import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.FramesToShow
import com.javernaut.whatthecodec.presentation.root.viewmodel.VideoInfoViewModel
import com.javernaut.whatthecodec.util.forEachChild
import com.javernaut.whatthecodec.util.setupTwoLineView
import kotlinx.android.synthetic.main.fragment_video_page.*
import kotlinx.android.synthetic.main.inline_video_left_panel.*
import kotlinx.android.synthetic.main.inline_video_right_panel.*

class VideoPageFragment : Fragment(R.layout.fragment_video_page) {

    private val videoInfoViewModel by activityViewModels<VideoInfoViewModel>()

    private var progressDialog: Dialog? = null

    private val framesNumberChangeListener = RadioGroup.OnCheckedChangeListener { _, checkedId ->
        videoInfoViewModel.setFramesToShow(when (checkedId) {
            R.id.framesNum9 -> FramesToShow.NINE
            R.id.framesNum4 -> FramesToShow.FOUR
            else -> FramesToShow.ONE
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        videoInfoViewModel.basicVideoInfoLiveData.observe(this, Observer {
            fileFormat.setupTwoLineView(R.string.info_file_format, it.fileFormat)
            codecName.setupTwoLineView(R.string.page_video_codec_name, it.codecName)
            width.setupTwoLineView(R.string.page_video_frame_width, it.frameWidth.toString())
            height.setupTwoLineView(R.string.page_video_frame_height, it.frameHeight.toString())
        })

        videoInfoViewModel.isFullFeaturedLiveData.observe(this, Observer { isFullFeatured ->
            protocol.setupTwoLineView(R.string.info_protocol_title, getString(
                    if (isFullFeatured) {
                        R.string.info_protocol_file
                    } else {
                        R.string.info_protocol_pipe
                    }))

            framesNumberGroup.forEachChild {
                it.isEnabled = isFullFeatured
            }

            framesNumberGroup.visibility = View.VISIBLE
        })

        videoInfoViewModel.framesToShowNumber.observe(this, Observer {
            framesNumberGroup.setOnCheckedChangeListener(null)
            framesNumberGroup.check(when (it) {
                FramesToShow.ONE -> R.id.framesNum1
                FramesToShow.FOUR -> R.id.framesNum4
                FramesToShow.NINE -> R.id.framesNum9
            })
            framesNumberGroup.setOnCheckedChangeListener(framesNumberChangeListener)
        })


        videoInfoViewModel.modalProgressLiveData.observe(this, Observer {
            progressDialog?.dismiss()
            progressDialog = if (it) {
                ProgressDialog.show(requireContext(), null, getString(R.string.message_progress))
            } else {
                null
            }
        })

        videoInfoViewModel.framesLiveData.observe(this, Observer {
            frameDisplayingView.setFrames(it)
        })

        videoInfoViewModel.framesBackgroundLiveData.observe(this, Observer { newColor ->
            val currentColor = (frameBackground.background as? ColorDrawable)?.color
                    ?: Color.TRANSPARENT
            ObjectAnimator.ofObject(frameBackground,
                    "backgroundColor",
                    ArgbEvaluator(),
                    currentColor,
                    newColor
            )
                    .setDuration(300)
                    .start()
        })
    }

    override fun onDestroy() {
        progressDialog?.dismiss()
        progressDialog = null
        super.onDestroy()
    }
}