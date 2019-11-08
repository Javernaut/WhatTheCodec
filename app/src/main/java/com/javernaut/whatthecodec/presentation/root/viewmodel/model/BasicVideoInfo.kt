package com.javernaut.whatthecodec.presentation.root.viewmodel.model

data class BasicVideoInfo(
        val fileFormat: String,
        val codecName: String,
        val frameWidth: Int,
        val frameHeight: Int
)