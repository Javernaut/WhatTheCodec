package com.javernaut.whatthecodec.home.presentation.model

import android.graphics.Bitmap

sealed class Frame

data object LoadingFrame : Frame()

data object PlaceholderFrame : Frame()

data object DecodingErrorFrame : Frame()

class ActualFrame(val frameData: Bitmap) : Frame()
