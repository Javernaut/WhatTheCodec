package com.javernaut.whatthecodec.compose.playground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.compose.theme.WhatTheCodecTheme
import com.javernaut.whatthecodec.home.ui.screen.ObserveAsEvents
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ComposePlaygroundActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            WhatTheCodecTheme.Dynamic {
                PlaygroundScreen()
            }
        }
    }
}

class PlaygroundViewModel : ViewModel() {

    private val _screenMessageChannel = Channel<PlaygroundScreenMessage>()
    val screenMessage = _screenMessageChannel.receiveAsFlow()

    fun doSomething() {
        viewModelScope.launch {
            _screenMessageChannel.send(PlaygroundScreenMessage.FileOpeningError)
        }
    }
}

sealed interface PlaygroundScreenMessage {
    data object FileOpeningError : PlaygroundScreenMessage
}

@Composable
private fun PlaygroundScreen(viewModel: PlaygroundViewModel = viewModel()) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    ObserveAsEvents(flow = viewModel.screenMessage) {
        when (it) {
            PlaygroundScreenMessage.FileOpeningError -> {
                scope.launch {
                    snackbarHostState.showSnackbar(context.getString(R.string.message_couldnt_open_file))
                }
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = viewModel::doSomething) {
                Text(text = "Do something")
            }
        }
    }
}
