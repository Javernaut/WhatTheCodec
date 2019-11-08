#include <jni.h>

#include "video_stream.h"
#include "frame_extractor.h"

// File with JNI bindings for VideoStream java class.

extern "C" {
JNIEXPORT void JNICALL
Java_com_javernaut_whatthecodec_domain_VideoStream_release(JNIEnv *, jobject instance) {
    video_stream_free(instance);
}


JNIEXPORT jboolean JNICALL
Java_com_javernaut_whatthecodec_domain_VideoStream_fillWithPreview(JNIEnv *env, jobject instance,
                                                                   jobjectArray jBitmaps) {
    bool areAllFramesOk = frame_extractor_fill_with_preview(env, instance, jBitmaps);
    return static_cast<jboolean>(areAllFramesOk);
}
}