//
// Created by Alex Javernaut on 3/24/19.
//

#include "unistd.h"

extern "C" {
#include <libavformat/avformat.h>
#include <libavcodec/avcodec.h>
}

#include "video_config.h"
#include "utils.h"

static VideoConfig *video_config_create(int fileDescriptor) {
    AVFormatContext *avFormatContext = nullptr;

    char str[32];
    sprintf(str, "pipe:%d", fileDescriptor);

    if (avformat_open_input(&avFormatContext, str, nullptr, nullptr)) {
        return nullptr;
    }

    auto *videoConfig = (VideoConfig *) malloc(sizeof(VideoConfig));;
    videoConfig->fileDescriptor = fileDescriptor;
    videoConfig->avFormatContext = avFormatContext;

    if (avformat_find_stream_info(avFormatContext, nullptr) < 0) {
        avformat_free_context(avFormatContext);
        return nullptr;
    };

    for (int pos = 0; pos < avFormatContext->nb_streams; pos++) {
        // Getting the name of a codec of the very first video stream
        AVCodecParameters *parameters = avFormatContext->streams[pos]->codecpar;
        if (parameters->codec_type == AVMEDIA_TYPE_VIDEO) {
            videoConfig->parameters = parameters;
            videoConfig->avVideoCodec = avcodec_find_decoder(parameters->codec_id);
            videoConfig->videoStreamIndex = pos;
            break;
        }
    }

    return videoConfig;
}

static void video_config_set_pointer(jobject thiz, jlong value) {
    utils_get_env()->SetLongField(thiz,
                                  fields.VideoFileConfig.nativePointer,
                                  value);
}

void video_config_new(jobject instance, int fd) {
    VideoConfig *videoConfig = video_config_create(fd);
    jlong valueToAttach = videoConfig == nullptr ? -1 : reinterpret_cast<long>(videoConfig);
    video_config_set_pointer(instance, valueToAttach);
}

VideoConfig *video_config_get(jobject jVideoConfig) {
    jlong nativePointer = utils_get_env()->GetLongField(jVideoConfig,
                                                        fields.VideoFileConfig.nativePointer);
    return reinterpret_cast<VideoConfig *>(nativePointer);
}

void video_config_free(jobject jVideoConfig) {
    auto *videoConfig = video_config_get(jVideoConfig);
    auto *avFormatContext = videoConfig->avFormatContext;

    avformat_close_input(&avFormatContext);
    close(videoConfig->fileDescriptor);

    free(videoConfig);

    video_config_set_pointer(jVideoConfig, -1);
}