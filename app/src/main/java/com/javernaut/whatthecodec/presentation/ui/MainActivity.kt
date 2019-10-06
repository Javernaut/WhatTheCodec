package com.javernaut.whatthecodec.presentation.ui

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.viewmodel.FramesToShow
import com.javernaut.whatthecodec.presentation.viewmodel.VideoInfoViewModel
import com.javernaut.whatthecodec.presentation.viewmodel.VideoInfoViewModelFactory
import com.javernaut.whatthecodec.util.TinyActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.inline_video_left_panel.*
import kotlinx.android.synthetic.main.inline_video_right_panel.*

class MainActivity : AppCompatActivity() {

    private val videoInfoViewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(
                this, VideoInfoViewModelFactory(this)
        ).get(VideoInfoViewModel::class.java)
    }

    private var progressDialog: Dialog? = null

    private val framesNumberChangeListener = RadioGroup.OnCheckedChangeListener { _, checkedId ->
        videoInfoViewModel.setFramesToShow(when (checkedId) {
            R.id.framesNum9 -> FramesToShow.NINE
            R.id.framesNum4 -> FramesToShow.FOUR
            else -> FramesToShow.ONE
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        videoInfoViewModel.basicInfoLiveData.observe(this, Observer {
            fileFormat.setupTwoLineView(R.string.info_file_format, it.fileFormat)
            videoCodec.setupTwoLineView(R.string.info_video_codec, it.codecName)
            width.setupTwoLineView(R.string.info_width, it.frameWidth.toString())
            height.setupTwoLineView(R.string.info_height, it.frameHeight.toString())
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

        videoInfoViewModel.errorMessageLiveEvent.observe(this, Observer {
            if (it) {
                toast(R.string.message_couldnt_open_file)
            }
        })

        videoInfoViewModel.modalProgressLiveData.observe(this, Observer {
            progressDialog?.dismiss()
            progressDialog = if (it) {
                ProgressDialog.show(this, null, getString(R.string.message_progress))
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

        onCheckForActionView()
    }

    override fun onDestroy() {
        progressDialog?.dismiss()
        progressDialog = null
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_PICK_VIDEO) {
            if (resultCode == RESULT_OK && data?.data != null) {
                tryGetVideoConfig(data.data!!)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_PERMISSION_ACTION_VIEW,
            REQUEST_CODE_PERMISSION_PICK -> {
                if (TinyActivityCompat.wasReadStoragePermissionGranted(permissions, grantResults)) {
                    if (requestCode == REQUEST_CODE_PERMISSION_ACTION_VIEW) {
                        actualDisplayFileFromActionView()
                    } else {
                        actualPickVideoFile()
                    }
                } else {
                    toast(R.string.message_permission_denied)
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(R.string.menu_pick_video).setOnMenuItemClickListener {
            onPickVideoClicked()
            true
        }.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        return true
    }

    private fun onPickVideoClicked() {
        if (TinyActivityCompat.needRequestReadStoragePermission(this)) {
            TinyActivityCompat.requestReadStoragePermission(this, REQUEST_CODE_PERMISSION_PICK)
        } else {
            actualPickVideoFile()
        }
    }

    private fun onCheckForActionView() {
        if (Intent.ACTION_VIEW == intent.action && intent.data != null) {
            if (TinyActivityCompat.needRequestReadStoragePermission(this@MainActivity)) {
                TinyActivityCompat.requestReadStoragePermission(this@MainActivity, REQUEST_CODE_PERMISSION_ACTION_VIEW)
            } else {
                actualDisplayFileFromActionView()
            }
        }
    }

    private fun actualPickVideoFile() {
        startActivityForResult(Intent(Intent.ACTION_GET_CONTENT)
                .setType("video/*")
                .putExtra(Intent.EXTRA_LOCAL_ONLY, true)
                .addCategory(Intent.CATEGORY_OPENABLE),
                REQUEST_CODE_PICK_VIDEO)
    }

    private fun actualDisplayFileFromActionView() {
        tryGetVideoConfig(intent.data!!)
    }

    private fun tryGetVideoConfig(uri: Uri) {
        videoInfoViewModel.tryGetVideoConfig(uri.toString())
    }

    private fun toast(msg: Int) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    private fun View.setupTwoLineView(text1: Int, text2: String) {
        findViewById<TextView>(android.R.id.text1).setText(text1)
        findViewById<TextView>(android.R.id.text2).text = text2
    }

    private inline fun ViewGroup.forEachChild(action: (View) -> Unit) {
        for (pos in 0 until childCount) {
            action(getChildAt(pos))
        }
    }

    companion object {
        private const val REQUEST_CODE_PICK_VIDEO = 42
        private const val REQUEST_CODE_PERMISSION_ACTION_VIEW = 43
        private const val REQUEST_CODE_PERMISSION_PICK = 44
    }
}
