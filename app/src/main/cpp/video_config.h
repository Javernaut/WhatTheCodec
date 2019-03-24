//
// Created by Alex Javernaut on 3/24/19.
//

#ifndef WHATTHECODEC_VIDEO_CONFIG_H
#define WHATTHECODEC_VIDEO_CONFIG_H

#include <jni.h>

struct VideoConfig {
    AVFormatContext *avFormatContext;
    AVCodecParameters *parameters;
    AVCodec *avVideoCodec;
};

// Setting the VideoFileConfig.nativePointer is delegated to Java part
VideoConfig *video_config_create(const char *filePath);

VideoConfig *video_config_get(jobject jVideoConfig);

void video_config_free(jobject jVideoConfig);

#endif //WHATTHECODEC_VIDEO_CONFIG_H
