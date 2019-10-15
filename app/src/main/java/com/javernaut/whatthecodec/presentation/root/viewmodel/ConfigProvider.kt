package com.javernaut.whatthecodec.presentation.root.viewmodel

import com.javernaut.whatthecodec.domain.VideoFileConfig

interface ConfigProvider {
    fun obtainConfig(uri: String): VideoFileConfig?
}