package com.javernaut.whatthecodec.presentation.root.viewmodel.model

import android.graphics.Bitmap

sealed class Frame

object LoadingFrame : Frame()

object PlaceholderFrame : Frame()

object DecodingErrorFrame : Frame()

class ActualFrame(val frameData: Bitmap) : Frame()
