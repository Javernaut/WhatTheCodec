package com.javernaut.whatthecodec.presentation.root.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.core.content.MimeTypeFilter
import androidx.lifecycle.ViewModelProvider
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.domain.MediaType
import com.javernaut.whatthecodec.presentation.compose.theme.WhatTheCodecTheme
import com.javernaut.whatthecodec.presentation.root.viewmodel.MediaFileArgument
import com.javernaut.whatthecodec.presentation.root.viewmodel.MediaFileViewModel
import com.javernaut.whatthecodec.presentation.root.viewmodel.MediaFileViewModelFactory
import com.javernaut.whatthecodec.presentation.video.ui.view.getDesiredFrameWidth
import com.javernaut.whatthecodec.util.TinyActivityCompat

class RootActivity : AppCompatActivity() {

    private val mediaFileViewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProvider(
            this, MediaFileViewModelFactory(this, getDesiredFrameWidth(this))
        ).get(MediaFileViewModel::class.java)
    }

    private var intentActionViewConsumed = false

//    private lateinit var pagerAdapter: RootPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WhatTheCodecTheme {
                val tabsToShow by mediaFileViewModel.availableTabsLiveData.observeAsState()
                if (tabsToShow == null) {
                    EmptyScreen(
                        onVideoIconClick = ::onPickVideoClicked,
                        onAudioIconClick = ::onPickAudioClicked
                    ) {
                        // TODO Add Settings menu item here
                    }
                } else {
                    // TODO
                }
            }
        }

//        pagerAdapter = RootPagerAdapter(this)
//        pager.adapter = pagerAdapter
//        TabLayoutMediator(tabs, pager, pagerAdapter.tabConfigurationStrategy).attach()

//        mediaFileViewModel.availableTabsLiveData.observe(this) {
//            tabs.visibility = View.VISIBLE
//            supportActionBar?.title = null
//
//            pagerAdapter.availableTabs = it
//
//            emptyComposeView.setVisible(false)
//            pager.setVisible(true)
//
//            invalidateOptionsMenu()
//        }

        mediaFileViewModel.errorMessageLiveEvent.observe(this) {
            if (it) {
                toast(R.string.message_couldnt_open_file)
            }
        }

        intentActionViewConsumed =
            savedInstanceState?.getBoolean(EXTRA_INTENT_ACTION_VIEW_CONSUMED) == true
        if (!intentActionViewConsumed) {
            onCheckForActionView()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        intentActionViewConsumed = false
        onCheckForActionView()
    }

    override fun onResume() {
        super.onResume()
        mediaFileViewModel.applyPendingMediaFileIfNeeded()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(EXTRA_INTENT_ACTION_VIEW_CONSUMED, intentActionViewConsumed)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_PICK_VIDEO || requestCode == REQUEST_CODE_PICK_AUDIO) {
            if (resultCode == RESULT_OK && data?.data != null) {
                openMediaFile(
                    data.data!!, if (requestCode == REQUEST_CODE_PICK_VIDEO) {
                        MediaType.VIDEO
                    } else {
                        MediaType.AUDIO
                    }
                )
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_PERMISSION_ACTION_VIEW,
            REQUEST_CODE_PERMISSION_PICK_VIDEO,
            REQUEST_CODE_PERMISSION_PICK_AUDIO -> {
                if (TinyActivityCompat.wasReadStoragePermissionGranted(permissions, grantResults)) {
                    when (requestCode) {
                        REQUEST_CODE_PERMISSION_ACTION_VIEW -> actualDisplayFileFromActionView()
                        REQUEST_CODE_PERMISSION_PICK_VIDEO -> actualPickVideoFile()
                        else -> actualPickAudioFile()
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

    // TODO Pick values from here in actual menu
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        if (!emptyComposeView.isVisible()) {
//            addMenuItem(
//                menu,
//                R.string.menu_pick_video,
//                R.drawable.ic_menu_video,
//                MenuItem.SHOW_AS_ACTION_ALWAYS
//            ) {
//                onPickVideoClicked()
//            }
//            addMenuItem(
//                menu,
//                R.string.menu_pick_audio,
//                R.drawable.ic_menu_audio,
//                MenuItem.SHOW_AS_ACTION_ALWAYS
//            ) {
//                onPickAudioClicked()
//            }
//        }
//        addMenuItem(menu, R.string.menu_settings, 0, MenuItem.SHOW_AS_ACTION_NEVER) {
//            SettingsActivity.start(this)
//        }
//        return true
//    }

    private fun onPickVideoClicked() {
        checkPermissionAndTryOpenMedia(REQUEST_CODE_PERMISSION_PICK_VIDEO) {
            actualPickVideoFile()
        }
    }

    private fun onPickAudioClicked() {
        checkPermissionAndTryOpenMedia(REQUEST_CODE_PERMISSION_PICK_AUDIO) {
            actualPickAudioFile()
        }
    }

    private fun onCheckForActionView() {
        if (Intent.ACTION_VIEW == intent.action && intent.data != null) {
            checkPermissionAndTryOpenMedia(REQUEST_CODE_PERMISSION_ACTION_VIEW) {
                actualDisplayFileFromActionView()
            }
        }
    }

    private inline fun checkPermissionAndTryOpenMedia(requestCode: Int, actualAction: () -> Unit) {
        if (TinyActivityCompat.needRequestReadStoragePermission(this)) {
            TinyActivityCompat.requestReadStoragePermission(this, requestCode)
        } else {
            actualAction()
        }
    }

    private fun actualPickVideoFile() {
        startActivityForMediaFile(MIME_TYPE_VIDEO, REQUEST_CODE_PICK_VIDEO)
    }

    private fun actualPickAudioFile() {
        startActivityForMediaFile(MIME_TYPE_AUDIO, REQUEST_CODE_PICK_AUDIO)
    }

    private fun actualDisplayFileFromActionView() {
        intentActionViewConsumed = true
        openMediaFile(
            intent.data!!, if (MimeTypeFilter.matches(intent.type, MIME_TYPE_AUDIO)) {
                MediaType.AUDIO
            } else {
                MediaType.VIDEO
            }
        )
    }

    private fun startActivityForMediaFile(type: String, requestCode: Int) {
        startActivityForResult(
            Intent(Intent.ACTION_GET_CONTENT)
                .setType(type)
                .putExtra(Intent.EXTRA_LOCAL_ONLY, true)
                .addCategory(Intent.CATEGORY_OPENABLE),
            requestCode
        )
    }

    private fun openMediaFile(uri: Uri, mediaType: MediaType) {
        mediaFileViewModel.openMediaFile(MediaFileArgument(uri.toString(), mediaType))
    }

    private fun toast(msg: Int) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val REQUEST_CODE_PICK_VIDEO = 41
        private const val REQUEST_CODE_PICK_AUDIO = 42
        private const val REQUEST_CODE_PERMISSION_ACTION_VIEW = 43
        private const val REQUEST_CODE_PERMISSION_PICK_VIDEO = 44
        private const val REQUEST_CODE_PERMISSION_PICK_AUDIO = 45

        private const val EXTRA_INTENT_ACTION_VIEW_CONSUMED = "extra_intent_action_view_consumed"

        private const val MIME_TYPE_VIDEO = "video/*"
        private const val MIME_TYPE_AUDIO = "audio/*"
    }
}
