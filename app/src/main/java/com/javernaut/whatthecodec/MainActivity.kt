package com.javernaut.whatthecodec

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.FileNotFoundException

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.pick_video).setOnClickListener {
            startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT)
                    .setType("video/*")
                    .addCategory(Intent.CATEGORY_OPENABLE),
                    PICK_VIDEO_REQUEST_CODE)
        }

        frameDisplayingView.childFramesCount = 4
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == PICK_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                tryGetVideoConfig(data.data)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun tryGetVideoConfig(uri: Uri?) {
        var videoFileConfig: VideoFileConfig? = null
        try {
            val descriptor = contentResolver.openFileDescriptor(uri!!, "r")
            if (descriptor != null) {
                videoFileConfig = VideoFileConfig.create(descriptor)
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        if (videoFileConfig != null) {
            setVideoConfig(videoFileConfig)
            videoFileConfig.release()
        } else {
            toast("Couldn't open the file")
        }
    }

    private fun setVideoConfig(config: VideoFileConfig) {
        details_panel.visibility = View.VISIBLE
        file_format.text = config.fileFormat
        video_codec.text = config.codecName
        width.text = config.width.toString()
        height.text = config.height.toString()
        frameDisplayingView.setVideoConfig(config)
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val PICK_VIDEO_REQUEST_CODE = 42
    }
}
