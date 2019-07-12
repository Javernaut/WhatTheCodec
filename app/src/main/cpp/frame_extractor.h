//
// Created by Alex Javernaut on 2019-07-12.
//

#ifndef WHATTHECODEC_FRAME_EXTRACTOR_H
#define WHATTHECODEC_FRAME_EXTRACTOR_H

#include <jni.h>

void frame_extractor_play_with_colors(JNIEnv *env, jobject jBitmap);
void frame_extractor_fill_with_preview(JNIEnv *env, jobject jVideoConfig, jobjectArray jBitmaps);

#endif //WHATTHECODEC_FRAME_EXTRACTOR_H
