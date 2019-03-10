#include <jni.h>
#include <string>

extern "C" {
    #include <libavformat/avformat.h>
    #include <libavcodec/avcodec.h>
    #include <libavutil/error.h>
}

extern "C"
JNIEXPORT jstring

JNICALL
Java_com_javernaut_whatthecodec_MainActivity_getVideoCodecName(
        JNIEnv *env,
        jobject thiz,
        jstring filePath) {
    AVFormatContext *fmt_ctx = NULL;

    const char *cFilePath = env->GetStringUTFChars(filePath, 0);

    if (avformat_open_input(&fmt_ctx, cFilePath, NULL, NULL)) {
        return env->NewStringUTF("Couldn't open the file");
    }

    int pos = 0;
    const char *resultName = "Unknown Codec";
    while (pos < fmt_ctx->max_streams) {
        // Getting the name of a codec of the very first video stream
        if (fmt_ctx->streams[pos]->codecpar->codec_type == AVMEDIA_TYPE_VIDEO) {
            AVCodec *codec = avcodec_find_decoder(fmt_ctx->streams[pos]->codecpar->codec_id);
            resultName = codec->name;
            break;
        }
        pos++;
    }

    avformat_close_input(&fmt_ctx);
    env->ReleaseStringUTFChars(filePath, cFilePath);
    return env->NewStringUTF(resultName);
}
