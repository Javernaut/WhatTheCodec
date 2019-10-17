//
// Created by Alexander Berezhnoi on 14/10/19.
//

#include "video_stream.h"
#include "utils.h"

static void video_config_set_pointer(jobject jVideoConfig, jlong value) {
    utils_get_env()->SetLongField(jVideoConfig,
                                  fields.VideoStream.nativePointer,
                                  value);
}

VideoStream *video_stream_get(jobject jVideoConfig) {
    jlong nativePointer = utils_get_env()->GetLongField(jVideoConfig,
                                                        fields.VideoStream.nativePointer);
    return reinterpret_cast<VideoStream *>(nativePointer);
}

long video_stream_get_handle(VideoStream *videoStream) {
    return reinterpret_cast<long>(videoStream);
}

void video_stream_free(jobject jVideoConfig) {
    auto *videoConfig = video_stream_get(jVideoConfig);
    auto *avFormatContext = videoConfig->avFormatContext;

    avformat_close_input(&avFormatContext);

    free(videoConfig);

    video_config_set_pointer(jVideoConfig, -1);
}