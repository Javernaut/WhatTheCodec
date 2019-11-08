//
// Created by Alexander Berezhnoi on 12/07/19.
//

#ifndef WHATTHECODEC_FRAME_EXTRACTOR_H
#define WHATTHECODEC_FRAME_EXTRACTOR_H

#include <jni.h>

/**
 * Extracts N equidistant frames from the video, where N is jBitmaps.size. Typically 1, 4 or 9.
 * Decoded frames are scaled to the size of Bitmaps in jBitmaps.
 *
 * @param env a part of jBitmaps accessing
 * @param jVideoStream a VideoStream java object
 * @param jBitmaps an array of java Bitmap objects to write frames
 *
 * @return true if all frames were successfully extracted, false otherwise
 */
bool frame_extractor_fill_with_preview(JNIEnv *env, jobject jVideoStream, jobjectArray jBitmaps);

#endif //WHATTHECODEC_FRAME_EXTRACTOR_H
