//
// Created by Alex Javernaut on 3/24/19.
//

extern "C" {
#include <libavformat/avformat.h>
#include <libavcodec/avcodec.h>
}

#include "video_config.h"
#include "utils.h"

static VideoConfig *video_config_create(const char *filePath) {
    AVFormatContext *avFormatContext = nullptr;

    if (avformat_open_input(&avFormatContext, filePath, nullptr, nullptr)) {
        return nullptr;
    }

    auto *videoConfig = (VideoConfig *) malloc(sizeof(VideoConfig));;
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

void video_config_new(jobject instance, const char *filePath) {
    VideoConfig *videoConfig = video_config_create(filePath);
    jlong valueToAttach = videoConfig == nullptr ? -1 : reinterpret_cast<long>(videoConfig);
    video_config_set_pointer(instance, valueToAttach);
}

VideoConfig *video_config_get(jobject jVideoConfig) {
    // TODO implement me
    return nullptr;
}

void video_config_free(jobject jVideoConfig) {
    // TODO implement me
}