#include <jni.h>

#include "video_config.h"
#include "frame_extractor.h"

extern "C"
JNIEXPORT jstring JNICALL
Java_com_javernaut_whatthecodec_VideoFileConfig_getFileFormat(JNIEnv *env, jobject instance) {
    auto *videoConfig = video_config_get(instance);
    return env->NewStringUTF(videoConfig->avFormatContext->iformat->long_name);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_javernaut_whatthecodec_VideoFileConfig_release(JNIEnv *env, jobject instance) {
    video_config_free(instance);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_javernaut_whatthecodec_VideoFileConfig_getCodecName(JNIEnv *env, jobject instance) {
    auto *videoConfig = video_config_get(instance);
    return env->NewStringUTF(videoConfig->avVideoCodec->long_name);
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_javernaut_whatthecodec_VideoFileConfig_getWidth(JNIEnv *, jobject instance) {
    auto *videoConfig = video_config_get(instance);
    return videoConfig->parameters->width;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_javernaut_whatthecodec_VideoFileConfig_getHeight(JNIEnv *, jobject instance) {
    auto *videoConfig = video_config_get(instance);
    return videoConfig->parameters->height;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_javernaut_whatthecodec_VideoFileConfig_nativeNew(JNIEnv *, jobject instance,
                                                          jint jFileDescriptor) {
    video_config_new(instance, jFileDescriptor);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_javernaut_whatthecodec_VideoFileConfig_fillWithPreview(JNIEnv *env, jobject instance,
                                                                jobject jBitmap) {
    frame_extractor_fill_with_preview(env, instance, jBitmap);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_javernaut_whatthecodec_FrameDisplayingView_doTheThingWithBitmap(JNIEnv *env, jclass type,
                                                                         jobject bitmap) {
    frame_extractor_play_with_colors(env, bitmap);
}