package com.javernaut.whatthecodec.home.presentation.model

sealed interface ScreenMessage {
    data object FileOpeningError : ScreenMessage
    data object PermissionDeniedError : ScreenMessage
    class ValueCopied(val value: String) : ScreenMessage
}
