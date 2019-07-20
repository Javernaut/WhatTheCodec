package com.javernaut.whatthecodec

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.FileNotFoundException

class MainActivity : Activity() {

    private var videoFileConfig: VideoFileConfig? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pick_video.setOnClickListener {
            if (TinyActivityCompat.needRequestReadStoragePermission(this)) {
                TinyActivityCompat.requestReadStoragePermission(this, REQUEST_CODE_PERMISSION_PICK)
            } else {
                actualPickVideoFile()
            }
        }

        frames_num_group.setOnCheckedChangeListener { _, checkedId ->
            frameDisplayingView.childFramesCount = when (checkedId) {
                R.id.frames_num_9 -> 9
                R.id.frames_num_4 -> 4
                else -> 1
            }
        }

        checkForActionView()
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
                    toast("Permission denied")
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    private fun checkForActionView() {
        if (Intent.ACTION_VIEW == intent.action && intent.data != null) {
            frameDisplayingView.doOnPreDraw {
                if (TinyActivityCompat.needRequestReadStoragePermission(this@MainActivity)) {
                    TinyActivityCompat.requestReadStoragePermission(this@MainActivity, REQUEST_CODE_PERMISSION_ACTION_VIEW)
                } else {
                    actualDisplayFileFromActionView()
                }
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
            toast("Couldn't open the file")
        }
    }

    private fun setVideoConfig(config: VideoFileConfig) {
        videoFileConfig?.release()
        videoFileConfig = config

        details_panel.visibility = View.VISIBLE
        frames_num_group.visibility = View.VISIBLE
        file_format.text = config.fileFormat
        video_codec.text = config.codecName
        width.text = config.width.toString()
        height.text = config.height.toString()
        protocol.text = if (config.fullFeatured) {
            "file"
        } else {
            "pipe"
        }
        frameDisplayingView.setVideoConfig(config)
    }

    private fun toast(msg: String) {
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

    companion object {
        private const val REQUEST_CODE_PICK_VIDEO = 42
        private const val REQUEST_CODE_PERMISSION_ACTION_VIEW = 43
        private const val REQUEST_CODE_PERMISSION_PICK = 44

    }
}
