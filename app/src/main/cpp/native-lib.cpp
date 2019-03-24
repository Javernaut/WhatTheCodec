#include <jni.h>
#include <string>

extern "C" {
#include <libavformat/avformat.h>
#include <libavcodec/avcodec.h>
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_javernaut_whatthecodec_VideoFileConfig_nativeCreate(JNIEnv *env, jclass type,
                                                             jstring jfilePath) {
    const char *filePath = env->GetStringUTFChars(jfilePath, nullptr);

    AVFormatContext *avFormatContext = nullptr;

    if (avformat_open_input(&avFormatContext, filePath, nullptr, nullptr)) {
        return -1;
    }

    env->ReleaseStringUTFChars(jfilePath, filePath);

    return (intptr_t) avFormatContext;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_javernaut_whatthecodec_VideoFileConfig_nativeGetFileFormat(JNIEnv *env, jclass type,
                                                                    jlong nativePointer) {
    auto *avFormatContext = reinterpret_cast<AVFormatContext *>(nativePointer);
    return env->NewStringUTF(avFormatContext->iformat->long_name);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_javernaut_whatthecodec_VideoFileConfig_release__J(JNIEnv *env, jclass type,
                                                           jlong nativePointer) {

    auto *avFormatContext = reinterpret_cast<AVFormatContext *>(nativePointer);
    avformat_close_input(&avFormatContext);
}