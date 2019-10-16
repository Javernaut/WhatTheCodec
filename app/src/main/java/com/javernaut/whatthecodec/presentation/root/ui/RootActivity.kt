package com.javernaut.whatthecodec.presentation.root.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.root.viewmodel.VideoInfoViewModel
import com.javernaut.whatthecodec.presentation.root.viewmodel.VideoInfoViewModelFactory
import com.javernaut.whatthecodec.util.TinyActivityCompat
import kotlinx.android.synthetic.main.root_main.*

class RootActivity : AppCompatActivity(R.layout.root_main) {

    private val videoInfoViewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(
                this, VideoInfoViewModelFactory(this)
        ).get(VideoInfoViewModel::class.java)
    }

    private var intentActionViewConsumed = false

    private lateinit var pagerAdapter: RootPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)

        pagerAdapter = RootPagerAdapter(supportFragmentManager, resources)
        pager.adapter = pagerAdapter
        tabs.setupWithViewPager(pager)

        videoInfoViewModel.availableTabsLiveData.observe(this, Observer {
            tabs.visibility = View.VISIBLE
            supportActionBar?.title = null

            pagerAdapter.availableTabs = it
        })

        videoInfoViewModel.errorMessageLiveEvent.observe(this, Observer {
            if (it) {
                toast(R.string.message_couldnt_open_file)
            }
        })

        intentActionViewConsumed = savedInstanceState?.getBoolean(EXTRA_INTENT_ACTION_VIEW_CONSUMED) == true
        if (!intentActionViewConsumed) {
            onCheckForActionView()
        }
    }

    override fun onResume() {
        super.onResume()
        videoInfoViewModel.applyPendingVideoConfigIfNeeded()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(EXTRA_INTENT_ACTION_VIEW_CONSUMED, intentActionViewConsumed)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_PICK_VIDEO) {
            if (resultCode == RESULT_OK && data?.data != null) {
                openVideoConfig(data.data!!)
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
            if (TinyActivityCompat.needRequestReadStoragePermission(this@RootActivity)) {
                TinyActivityCompat.requestReadStoragePermission(this@RootActivity, REQUEST_CODE_PERMISSION_ACTION_VIEW)
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
        intentActionViewConsumed = true
        openVideoConfig(intent.data!!)
    }

    private fun openVideoConfig(uri: Uri) {
        videoInfoViewModel.openVideoConfig(uri.toString())
    }

    private fun toast(msg: Int) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val REQUEST_CODE_PICK_VIDEO = 42
        private const val REQUEST_CODE_PERMISSION_ACTION_VIEW = 43
        private const val REQUEST_CODE_PERMISSION_PICK = 44

        private const val EXTRA_INTENT_ACTION_VIEW_CONSUMED = "extra_intent_action_view_consumed"
    }
}
