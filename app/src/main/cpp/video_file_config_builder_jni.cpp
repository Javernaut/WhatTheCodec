#include <jni.h>

#include "video_file_config_builder.h"
#include "frame_extractor.h"

// File with JNI bindings for MediaFileBuilder java class.

extern "C" {
JNIEXPORT void JNICALL
Java_com_javernaut_whatthecodec_domain_MediaFileBuilder_nativeCreateFromFD(JNIEnv *,
                                                                           jobject instance,
                                                                           jint jFileDescriptor,
                                                                           jint mediaStreamsMask) {
    video_file_config_build(instance, jFileDescriptor, mediaStreamsMask);
}

JNIEXPORT void JNICALL
Java_com_javernaut_whatthecodec_domain_MediaFileBuilder_nativeCreateFromPath(JNIEnv *env,
                                                                             jobject instance,
                                                                             jstring jFilePath,
                                                                             jint mediaStreamsMask) {
    const char *filePath = env->GetStringUTFChars(jFilePath, nullptr);

    video_file_config_build(instance, filePath, mediaStreamsMask);

    env->ReleaseStringUTFChars(jFilePath, filePath);
}
}