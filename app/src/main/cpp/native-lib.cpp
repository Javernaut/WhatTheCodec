#include <jni.h>
#include <string>

extern "C" {
#include <libavformat/avformat.h>
#include <libavcodec/avcodec.h>
}

struct VideoConfig {
    AVFormatContext *avFormatContext;
    AVCodec *avVideoCodec;
};

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

    auto *videoConfig = (VideoConfig *) malloc(sizeof(VideoConfig));;
    videoConfig->avFormatContext = avFormatContext;

    if (avformat_find_stream_info(avFormatContext, nullptr) < 0) {
        return -1;
    };

    for (int pos = 0; pos < avFormatContext->nb_streams; pos++) {
        // Getting the name of a codec of the very first video stream
        AVCodecParameters *parameters = avFormatContext->streams[pos]->codecpar;
        if (parameters->codec_type == AVMEDIA_TYPE_VIDEO) {
            videoConfig->avVideoCodec = avcodec_find_decoder(parameters->codec_id);
            break;
        }
    }

    return reinterpret_cast<long>(videoConfig);
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