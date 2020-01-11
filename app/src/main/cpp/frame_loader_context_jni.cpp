#include <jni.h>

#include "frame_loader_context.h"
#include "frame_extractor.h"

// File with JNI bindings for FrameLoader java class.

extern "C" {
JNIEXPORT void JNICALL
Java_com_javernaut_whatthecodec_domain_FrameLoader_nativeRelease(JNIEnv *, jclass,
                                                           jlong jFrameLoaderContextHandle) {
    frame_loader_context_free(jFrameLoaderContextHandle);
}


JNIEXPORT jboolean JNICALL
Java_com_javernaut_whatthecodec_domain_FrameLoader_nativeLoadFrame(JNIEnv *env, jclass,
                                                                   jlong jFrameLoaderContextHandle,
                                                                   jint index,
                                                                   jobject jBitmap) {
    bool successfullyLoaded = frame_extractor_load_frame(env, jFrameLoaderContextHandle, index, jBitmap);
    return static_cast<jboolean>(successfullyLoaded);
}
}