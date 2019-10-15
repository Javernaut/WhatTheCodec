#include <jni.h>

#include "video_file_config_builder.h"
#include "frame_extractor.h"

// File with JNI bindings for VideoFileConfigBuilder java class.

extern "C" {
JNIEXPORT void JNICALL
Java_com_javernaut_whatthecodec_domain_VideoFileConfigBuilder_nativeCreateFromFD(JNIEnv *,
                                                                                 jobject instance,
                                                                                 jint jFileDescriptor) {
    video_file_config_build(instance, jFileDescriptor);
}

JNIEXPORT void JNICALL
Java_com_javernaut_whatthecodec_domain_VideoFileConfigBuilder_nativeCreateFromPath(JNIEnv *env,
                                                                                   jobject instance,
                                                                                   jstring jFilePath) {
    const char *filePath = env->GetStringUTFChars(jFilePath, nullptr);

    video_file_config_build(instance, filePath);

    env->ReleaseStringUTFChars(jFilePath, filePath);
}
}