//
// Created by Alex Javernaut on 2019-07-12.
//

#ifndef WHATTHECODEC_FRAME_EXTRACTOR_H
#define WHATTHECODEC_FRAME_EXTRACTOR_H

#include <jni.h>

/** Returns true if all frames were successfully extracted, otherwise it returns false. */
bool frame_extractor_fill_with_preview(JNIEnv *env, jobject jVideoConfig, jobjectArray jBitmaps);

#endif //WHATTHECODEC_FRAME_EXTRACTOR_H
