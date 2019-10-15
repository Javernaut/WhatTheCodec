//
// Created by Alexander Berezhnoi on 12/07/19.
//

#include "frame_extractor.h"

extern "C" {
#include <libavformat/avformat.h>
#include <libavcodec/avcodec.h>
#include <libswscale/swscale.h>
#include <libavutil/imgutils.h>
}

#include <android/bitmap.h>
#include "video_stream.h"

bool frame_extractor_fill_with_preview(JNIEnv *env, jobject jVideoStream, jobjectArray jBitmaps) {
    int arraySize = env->GetArrayLength(jBitmaps);

    AndroidBitmapInfo bitmapMetricInfo;
    AndroidBitmap_getInfo(env, env->GetObjectArrayElement(jBitmaps, 0), &bitmapMetricInfo);

    auto *videoStream = video_stream_get(jVideoStream);

    auto pixelFormat = static_cast<AVPixelFormat>(videoStream->parameters->format);
    if (pixelFormat == AV_PIX_FMT_NONE) {
        // With pipe protocol some files fail to provide pixel format info.
        // In this case we can't establish neither scaling nor even a frame extracting.
        return false;
    }

    bool resultValue = true;

    SwsContext *scalingContext =
            sws_getContext(
                    // srcW
                    videoStream->parameters->width,
                    // srcH
                    videoStream->parameters->height,
                    // srcFormat
                    pixelFormat,
                    // dstW
                    bitmapMetricInfo.width,
                    // dstH
                    bitmapMetricInfo.height,
                    // dstFormat
                    AV_PIX_FMT_RGBA,
                    SWS_BICUBIC, nullptr, nullptr, nullptr);

    int64_t videoDuration = videoStream->
            avFormatContext->
            streams[videoStream->videoStreamIndex]->
            duration;

    for (int pos = 0; pos < arraySize; pos++) {
        jobject jBitmap = env->GetObjectArrayElement(jBitmaps, pos);

        AVPacket *packet = av_packet_alloc();
        AVFrame *frame = av_frame_alloc();

        int64_t seekPosition = videoDuration / arraySize * pos;
        // We call the av_seek_frame() for 0 position only if the media was opened for file path.
        // In case of file descriptor we omit this, as it can lead to reading error.
        bool fullFeatured = video_stream_is_full_featured(jVideoStream);
        if (fullFeatured || seekPosition > 0) {
            av_seek_frame(videoStream->avFormatContext,
                          videoStream->videoStreamIndex,
                          seekPosition,
                          0);
        }

        AVCodecContext *videoCodecContext = avcodec_alloc_context3(videoStream->avVideoCodec);
        avcodec_parameters_to_context(videoCodecContext, videoStream->parameters);
        avcodec_open2(videoCodecContext, videoStream->avVideoCodec, nullptr);

        while (true) {
            if (av_read_frame(videoStream->avFormatContext, packet) < 0) {
                // Couldn't read a packet, so we skip the whole frame
                resultValue = false;
                break;
            }

            if (packet->stream_index == videoStream->videoStreamIndex) {
                avcodec_send_packet(videoCodecContext, packet);
                int response = avcodec_receive_frame(videoCodecContext, frame);
                if (response == AVERROR(EAGAIN)) {
                    // A frame can be split across several packets, so continue reading in this case
                    continue;
                }

                if (response >= 0) {
                    AVFrame *frameForDrawing = av_frame_alloc();
                    void *bitmapBuffer;
                    AndroidBitmap_lockPixels(env, jBitmap, &bitmapBuffer);

                    // Prepare a FFmpeg's frame to use Android Bitmap's buffer
                    av_image_fill_arrays(
                            frameForDrawing->data,
                            frameForDrawing->linesize,
                            static_cast<const uint8_t *>(bitmapBuffer),
                            AV_PIX_FMT_RGBA,
                            bitmapMetricInfo.width,
                            bitmapMetricInfo.height,
                            1);

                    // Scale the frame that was read from the media to a frame that wraps Android Bitmap's buffer
                    sws_scale(
                            scalingContext,
                            frame->data,
                            frame->linesize,
                            0,
                            videoStream->parameters->height,
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
        avcodec_free_context(&videoCodecContext);
    }

    sws_freeContext(scalingContext);

    return resultValue;
}
