//
// Created by Alexander Berezhnoi on 14/10/19.
//

#include "video_stream.h"
#include "utils.h"

static void video_config_set_pointer(jobject jVideoStream, jlong value) {
    utils_get_env()->SetLongField(jVideoStream,
                                  fields.VideoStream.nativePointer,
                                  value);
}

VideoStream *video_stream_get(jobject jVideoStream) {
    jlong nativePointer = utils_get_env()->GetLongField(jVideoStream,
                                                        fields.VideoStream.nativePointer);
    return reinterpret_cast<VideoStream *>(nativePointer);
}

int64_t video_stream_get_handle(VideoStream *videoStream) {
    return reinterpret_cast<int64_t>(videoStream);
}

void video_stream_free(jobject jVideoStream) {
    auto *videoStream = video_stream_get(jVideoStream);
    auto *avFormatContext = videoStream->avFormatContext;

    avformat_close_input(&avFormatContext);

    free(videoStream);

    video_config_set_pointer(jVideoStream, -1);
}