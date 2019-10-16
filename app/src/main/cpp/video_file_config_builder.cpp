//
// Created by Alexander Berezhnoi on 24/03/19.
//

#include "video_file_config_builder.h"
#include "video_stream.h"
#include "utils.h"

extern "C" {
#include <libavformat/avformat.h>
#include <libavcodec/avcodec.h>
}

static jstring get_string(AVDictionary *metadata, const char *key) {
    jstring result = nullptr;
    AVDictionaryEntry *tag = av_dict_get(metadata, key, nullptr, 0);
    if (tag != nullptr) {
        result = utils_get_env()->NewStringUTF(tag->value);
    }
    return result;
}

static jstring get_title(AVDictionary *metadata) {
    return get_string(metadata, "title");
}

static jstring get_language(AVDictionary *metadata) {
    return get_string(metadata, "language");
}

static void onError(jobject jVideoFileConfigBuilder) {
    utils_call_instance_method(jVideoFileConfigBuilder,
                               fields.VideoFileConfigBuilder.onErrorID);
}

static void onVideoConfigFound(jobject jVideoFileConfigBuilder, AVFormatContext *avFormatContext) {
    const char *fileFormatName = avFormatContext->iformat->long_name;

    jstring jFileFormatName = utils_get_env()->NewStringUTF(fileFormatName);

    utils_call_instance_method(jVideoFileConfigBuilder,
                               fields.VideoFileConfigBuilder.onVideoConfigFoundID,
                               jFileFormatName);
}

static void onVideoStreamFound(jobject jVideoFileConfigBuilder,
                               AVFormatContext *avFormatContext,
                               int index) {
    AVCodecParameters *parameters = avFormatContext->streams[index]->codecpar;

    auto *videoStream = (VideoStream *) malloc(sizeof(VideoStream));;
    videoStream->avFormatContext = avFormatContext;
    videoStream->parameters = parameters;
    videoStream->avVideoCodec = avcodec_find_decoder(parameters->codec_id);
    videoStream->videoStreamIndex = index;

    jstring jCodecName = utils_get_env()->NewStringUTF(videoStream->avVideoCodec->long_name);

    utils_call_instance_method(jVideoFileConfigBuilder,
                               fields.VideoFileConfigBuilder.onVideoStreamFoundID,
                               parameters->width,
                               parameters->height,
                               jCodecName,
                               video_stream_get_handle(videoStream));
}

static void onAudioStreamFound(jobject jVideoFileConfigBuilder,
                               AVFormatContext *avFormatContext,
                               int index) {
    AVStream *stream = avFormatContext->streams[index];
    AVCodecParameters *parameters = stream->codecpar;

    auto codecDescriptor = avcodec_descriptor_get(parameters->codec_id);
    jstring jCodecName = utils_get_env()->NewStringUTF(codecDescriptor->long_name);

    utils_call_instance_method(jVideoFileConfigBuilder,
                               fields.VideoFileConfigBuilder.onAudioStreamFoundID,
                               index,
                               jCodecName,
                               get_title(stream->metadata),
                               get_language(stream->metadata),
                               stream->disposition);
}

// uri can be either file: or pipe:
void video_file_config_build(jobject jVideoFileConfigBuilder, const char *uri) {
    AVFormatContext *avFormatContext = nullptr;

    if (avformat_open_input(&avFormatContext, uri, nullptr, nullptr)) {
        onError(jVideoFileConfigBuilder);
        return;
    }

    if (avformat_find_stream_info(avFormatContext, nullptr) < 0) {
        avformat_free_context(avFormatContext);
        onError(jVideoFileConfigBuilder);
        return;
    };

    onVideoConfigFound(jVideoFileConfigBuilder, avFormatContext);

    for (int pos = 0; pos < avFormatContext->nb_streams; pos++) {
        AVCodecParameters *parameters = avFormatContext->streams[pos]->codecpar;
        AVMediaType type = parameters->codec_type;
        if (type == AVMEDIA_TYPE_VIDEO) {
            onVideoStreamFound(jVideoFileConfigBuilder, avFormatContext, pos);
        } else if (type == AVMEDIA_TYPE_AUDIO) {
            onAudioStreamFound(jVideoFileConfigBuilder, avFormatContext, pos);
        }
    }
}

void video_file_config_build(jobject jVideoFileConfigBuilder, int fileDescriptor) {
    char str[32];
    sprintf(str, "pipe:%d", fileDescriptor);

    video_file_config_build(jVideoFileConfigBuilder, str);
}