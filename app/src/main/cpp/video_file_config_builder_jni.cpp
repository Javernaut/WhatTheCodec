#include <jni.h>

#include "video_file_config_builder.h"
#include "frame_extractor.h"

// File with JNI bindings for MediaFileBuilder java class.

extern "C" {
JNIEXPORT void JNICALL
Java_com_javernaut_whatthecodec_domain_MediaFileBuilder_nativeCreateFromFD(JNIEnv *,
                                                                           jobject instance,
                                                                           jint fileDescriptor,
                                                                           jint mediaStreamsMask) {
    video_file_config_build(instance, fileDescriptor, mediaStreamsMask);
}

JNIEXPORT void JNICALL
Java_com_javernaut_whatthecodec_domain_MediaFileBuilder_nativeCreateFromAssetFD(JNIEnv *env,
                                                                                jobject instance,
                                                                                jint assetFileDescriptor,
                                                                                jlong startOffset,
                                                                                jstring jShortFormatName,
                                                                                jint mediaStreamsMask) {
    const char *cShortFormatName = env->GetStringUTFChars(jShortFormatName, nullptr);

    video_file_config_build(instance, assetFileDescriptor, startOffset, cShortFormatName, mediaStreamsMask);

    env->ReleaseStringUTFChars(jShortFormatName, cShortFormatName);
}

JNIEXPORT void JNICALL
Java_com_javernaut_whatthecodec_domain_MediaFileBuilder_nativeCreateFromPath(JNIEnv *env,
                                                                             jobject instance,
                                                                             jstring jFilePath,
                                                                             jint mediaStreamsMask) {
    const char *cFilePath = env->GetStringUTFChars(jFilePath, nullptr);

    video_file_config_build(instance, cFilePath, mediaStreamsMask);

    env->ReleaseStringUTFChars(jFilePath, cFilePath);
}
}