package com.javernaut.whatthecodec

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.FileNotFoundException

class MainActivity : AppCompatActivity() {

    private var videoFileConfig: VideoFileConfig? = null

    private val framesNumberChangeListener = RadioGroup.OnCheckedChangeListener { _, checkedId ->
        frameDisplayingView.childFramesCount = when (checkedId) {
            R.id.framesNum9 -> 9
            R.id.framesNum4 -> 4
            else -> 1
        }
        frameDisplayingView.loadPreviews()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        framesNumberGroup.setOnCheckedChangeListener(framesNumberChangeListener)

        onCheckForActionView()
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
        var videoFileConfig: VideoFileConfig? = null

        // First, try get a file:// path
        val path = PathUtil.getPath(this, uri)
        if (path != null) {
            videoFileConfig = VideoFileConfig.create(path)
        }

        // Second, try get a FileDescriptor.
        if (videoFileConfig == null) {
            try {
                val descriptor = contentResolver.openFileDescriptor(uri, "r")
                if (descriptor != null) {
                    videoFileConfig = VideoFileConfig.create(descriptor)
                }
            } catch (e: FileNotFoundException) {
            }
        }

        if (videoFileConfig != null) {
            setVideoConfig(videoFileConfig)
        } else {
            toast(R.string.message_couldnt_open_file)
        }
    }

    private fun setVideoConfig(config: VideoFileConfig) {
        videoFileConfig?.release()
        videoFileConfig = config

        framesNumberGroup.visibility = View.VISIBLE

        fileFormat.setupTwoLineView(R.string.info_file_format, config.fileFormat)
        videoCodec.setupTwoLineView(R.string.info_video_codec, config.codecName)
        width.setupTwoLineView(R.string.info_width, config.width.toString())
        height.setupTwoLineView(R.string.info_height, config.height.toString())
        protocol.setupTwoLineView(R.string.info_protocol_title, getString(
                if (config.fullFeatured) {
                    R.string.info_protocol_file
                } else {
                    R.string.info_protocol_pipe
                }))

        framesNumberGroup.forEachChild {
            it.isEnabled = config.fullFeatured
        }
        if (!config.fullFeatured) {
            force4FramesToShow()
        }
        frameDisplayingView.setVideoConfig(config)
        // This deferring is used here because loading previews needs to know the actual width of the view
        frameDisplayingView.doOnPreDraw {
            frameDisplayingView.loadPreviews()
        }
    }

    private fun force4FramesToShow() {
        framesNumberGroup.setOnCheckedChangeListener(null)
        framesNumberGroup.check(R.id.framesNum4)
        framesNumberGroup.setOnCheckedChangeListener(framesNumberChangeListener)
        frameDisplayingView.childFramesCount = 4
    }

    private fun toast(msg: Int) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    private inline fun View.doOnPreDraw(crossinline action: () -> Unit) {
        viewTreeObserver.addOnPreDrawListener(
                object : ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        viewTreeObserver.removeOnPreDrawListener(this)

                        action()

                        return true
                    }
                })
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
