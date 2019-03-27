#include <jni.h>
#include <string>

extern "C" {
#include <libavformat/avformat.h>
#include <libavcodec/avcodec.h>
}

#include "video_config.h"
#include "utils.h"

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    if (utils_fields_init(vm) != 0) {
        return -1;
    }
    return JNI_VERSION_1_6;
}

void JNI_OnUnload(JavaVM *vm, void *reserved) {
    utils_fields_free(vm);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_javernaut_whatthecodec_VideoFileConfig_nativeGetFileFormat(JNIEnv *env, jclass type,
                                                                    jlong nativePointer) {
    auto *videoConfig = reinterpret_cast<VideoConfig *>(nativePointer);
    auto *avFormatContext = videoConfig->avFormatContext;

    return env->NewStringUTF(avFormatContext->iformat->long_name);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_javernaut_whatthecodec_VideoFileConfig_release(JNIEnv *env, jclass type,
                                                        jlong nativePointer) {
    auto *videoConfig = reinterpret_cast<VideoConfig *>(nativePointer);
    auto *avFormatContext = videoConfig->avFormatContext;

    avformat_close_input(&avFormatContext);
    free(videoConfig);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_javernaut_whatthecodec_VideoFileConfig_getCodecName(JNIEnv *env, jobject instance) {
    auto *videoConfig = video_config_get(instance);
    return env->NewStringUTF(videoConfig->avVideoCodec->long_name);
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_javernaut_whatthecodec_VideoFileConfig_nativeGetWidth(JNIEnv *env, jclass type,
                                                               jlong nativePointer) {
    auto *videoConfig = reinterpret_cast<VideoConfig *>(nativePointer);
    return videoConfig->parameters->width;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_javernaut_whatthecodec_VideoFileConfig_nativeGetHeight(JNIEnv *env, jclass type,
                                                                jlong nativePointer) {
    auto *videoConfig = reinterpret_cast<VideoConfig *>(nativePointer);
    return videoConfig->parameters->height;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_javernaut_whatthecodec_VideoFileConfig_nativeNew(JNIEnv *env, jobject instance,
                                                          jstring jfilePath) {
    const char *filePath = env->GetStringUTFChars(jfilePath, nullptr);

    video_config_new(instance, filePath);

    env->ReleaseStringUTFChars(jfilePath, filePath);
}