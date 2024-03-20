package com.javernaut.whatthecodec.presentation.compose.playground

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.javernaut.whatthecodec.presentation.compose.theme3.WhatTheCodecM3Theme

class ComposePlaygroundActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WhatTheCodecM3Theme {

            }
        }
    }
}
