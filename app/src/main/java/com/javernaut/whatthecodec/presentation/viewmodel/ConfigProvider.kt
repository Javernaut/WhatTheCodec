package com.javernaut.whatthecodec.presentation.viewmodel

import com.javernaut.whatthecodec.domain.VideoFileConfig

interface ConfigProvider {
    fun obtainConfig(uri: String): VideoFileConfig?
}