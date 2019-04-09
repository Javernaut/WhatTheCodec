#include <jni.h>
#include <string>
#include <android/bitmap.h>

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
Java_com_javernaut_whatthecodec_VideoFileConfig_nativeNew(JNIEnv *env, jobject instance,
                                                          jstring jfilePath) {
    const char *filePath = env->GetStringUTFChars(jfilePath, nullptr);

    video_config_new(instance, filePath);

    env->ReleaseStringUTFChars(jfilePath, filePath);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_javernaut_whatthecodec_FrameDisplayingView_doTheThingWithBitmap(JNIEnv *env, jclass type,
                                                                         jobject bitmap) {
    AndroidBitmapInfo info;
    void *pixels;

    if (AndroidBitmap_getInfo(env, bitmap, &info) < 0) {
        return;
    }

    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        return;
    }

    if (AndroidBitmap_lockPixels(env, bitmap, &pixels) < 0) {
        LOGV("The pixel format isn't RGBA_8888");
    }

    auto *charPixels = static_cast<unsigned char *>(pixels);
    for (int i = 0; i < info.height; i++) {
        for (int j = 0; j < info.width; j++) {
            unsigned char *pixelPart = charPixels + i * info.stride + j * 4;
            int index = 0;
            if (i > info.height / 2) {
                index++;
            }
            if (j > info.width / 2) {
                index++;
            }
            *(pixelPart + index) = (unsigned char) 0x00;
        }
    }
    AndroidBitmap_unlockPixels(env, bitmap);
}