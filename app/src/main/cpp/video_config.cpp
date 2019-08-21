//
// Created by Alex Javernaut on 3/24/19.
//

#include "unistd.h"

#include "video_config.h"
#include "utils.h"

// uri can be either file: or pipe:
static VideoConfig *video_config_create(const char *uri) {
    AVFormatContext *avFormatContext = nullptr;

    if (avformat_open_input(&avFormatContext, uri, nullptr, nullptr)) {
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
            videoConfig->videoStreamIndex = pos;
            break;
        }
    }

    videoConfig->fullFeatured = true;

    return videoConfig;
}

static VideoConfig *video_config_create(int fileDescriptor) {
    char str[32];
    sprintf(str, "pipe:%d", fileDescriptor);

    VideoConfig *videoConfig = video_config_create(str);
    if (videoConfig != nullptr) {
        videoConfig->fileDescriptor = fileDescriptor;
        videoConfig->fullFeatured = false;
    }

    return videoConfig;
}

static void video_config_set_pointer(jobject jVideoConfig, jlong value) {
    utils_get_env()->SetLongField(jVideoConfig,
                                  fields.VideoFileConfig.nativePointer,
                                  value);
}

static void video_config_attach_pointer(jobject instance, VideoConfig *videoConfig) {
    jlong valueToAttach = videoConfig == nullptr ? -1 : reinterpret_cast<long>(videoConfig);
    video_config_set_pointer(instance, valueToAttach);
}

void video_config_new(jobject instance, int fd) {
    video_config_attach_pointer(instance, video_config_create(fd));
}

void video_config_new(jobject instance, const char *filePath) {
    video_config_attach_pointer(instance, video_config_create(filePath));
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
    if (!videoConfig->fullFeatured) {
        close(videoConfig->fileDescriptor);
    }

    free(videoConfig);

    video_config_set_pointer(jVideoConfig, -1);
}