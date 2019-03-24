#include <jni.h>
#include <string>

extern "C" {
#include <libavformat/avformat.h>
#include <libavcodec/avcodec.h>
}

#include "video_config.h"

extern "C"
JNIEXPORT jlong JNICALL
Java_com_javernaut_whatthecodec_VideoFileConfig_nativeCreate(JNIEnv *env, jclass type,
                                                             jstring jfilePath) {
    const char *filePath = env->GetStringUTFChars(jfilePath, nullptr);

    VideoConfig *videoConfig = video_config_create(filePath);

    env->ReleaseStringUTFChars(jfilePath, filePath);

    return videoConfig == nullptr ? -1 : reinterpret_cast<long>(videoConfig);
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
Java_com_javernaut_whatthecodec_VideoFileConfig_nativeGetCodecName(JNIEnv *env, jclass type,
                                                                   jlong nativePointer) {
    auto *videoConfig = reinterpret_cast<VideoConfig *>(nativePointer);
    auto *avVideoCodec = videoConfig->avVideoCodec;

    return env->NewStringUTF(avVideoCodec->long_name);
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