#include <jni.h>
#include <android/bitmap.h>

extern "C" {
#include <libavformat/avformat.h>
#include <libavcodec/avcodec.h>
#include <libswscale/swscale.h>
#include <libavutil/imgutils.h>
}

#include "video_config.h"
#include "log.h"

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
Java_com_javernaut_whatthecodec_VideoFileConfig_fillWithPreview(JNIEnv *env, jobject instance,
                                                                jobject jBitmap) {
    AndroidBitmapInfo bitmapInfo;
    AndroidBitmap_getInfo(env, jBitmap, &bitmapInfo);

    auto *videoConfig = video_config_get(instance);

    AVCodecContext *videoCodecContext = avcodec_alloc_context3(videoConfig->avVideoCodec);
    avcodec_parameters_to_context(videoCodecContext, videoConfig->parameters);
    avcodec_open2(videoCodecContext, videoConfig->avVideoCodec, nullptr);

    SwsContext *scalingContext =
            sws_getContext(
                    // srcW
                    videoConfig->parameters->width,
                    // srcH
                    videoConfig->parameters->height,
                    // srcFormat
                    videoCodecContext->pix_fmt,
                    // dstW
                    bitmapInfo.width,
                    // dstH
                    bitmapInfo.height,
                    // dstFormat
                    AV_PIX_FMT_RGBA,
                    SWS_BICUBIC, nullptr, nullptr, nullptr);

    AVPacket *packet = av_packet_alloc();
    AVFrame *frame = av_frame_alloc();

    while (av_read_frame(videoConfig->avFormatContext, packet) >= 0) {
        if (packet->stream_index == videoConfig->videoStreamIndex) {
            avcodec_send_packet(videoCodecContext, packet);
            int response = avcodec_receive_frame(videoCodecContext, frame);
            if (response == AVERROR(EAGAIN)) {
                continue;
            }

            if (response >= 0) {
                AVFrame *frameForDrawing = av_frame_alloc();
                void *bitmapBuffer;
                AndroidBitmap_lockPixels(env, jBitmap, &bitmapBuffer);

                av_image_fill_arrays(
                        frameForDrawing->data,
                        frameForDrawing->linesize,
                        static_cast<const uint8_t *>(bitmapBuffer),
                        AV_PIX_FMT_RGBA,
                        bitmapInfo.width,
                        bitmapInfo.height,
                        1);

                sws_scale(
                        scalingContext,
                        frame->data,
                        frame->linesize,
                        0,
                        videoConfig->parameters->height,
                        frameForDrawing->data,
                        frameForDrawing->linesize);

                av_frame_free(&frameForDrawing);

                AndroidBitmap_unlockPixels(env, jBitmap);
                break;
            }
        }
        av_packet_unref(packet);
    }

    av_packet_free(&packet);
    av_frame_free(&frame);
    sws_freeContext(scalingContext);
    avcodec_free_context(&videoCodecContext);
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