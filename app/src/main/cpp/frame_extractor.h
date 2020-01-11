//
// Created by Alexander Berezhnoi on 12/07/19.
//

#ifndef WHATTHECODEC_FRAME_EXTRACTOR_H
#define WHATTHECODEC_FRAME_EXTRACTOR_H

#include <jni.h>

/**
 * Loads one frame from the jFrameLoaderContextHandle at a specific index.
 * Decoded frame is scaled to the size of the Bitmap.
 *
 * @param env a part of jBitmap accessing
 * @param jFrameLoaderContextHandle a handle to FrameLoaderContext struct
 * @param index an index of a frame. Should be 0, 1, 2 or 3. For a single video the frame_extractor_load_frame() method
 * should be called 4 times. Indexes should be in increasing order.
 * @param jBitmap a destination to load a frame
 *
 * @return true if the frame was successfully loaded, false otherwise
 */
bool frame_extractor_load_frame(JNIEnv *env, int64_t jFrameLoaderContextHandle, int index, jobject jBitmap);

#endif //WHATTHECODEC_FRAME_EXTRACTOR_H
