//
// Created by Alex Javernaut on 3/24/19.
//

#ifndef WHATTHECODEC_VIDEO_CONFIG_H
#define WHATTHECODEC_VIDEO_CONFIG_H

#include <jni.h>

struct VideoConfig {
    int fileDescriptor;
    AVFormatContext *avFormatContext;
    AVCodecParameters *parameters;
    AVCodec *avVideoCodec;
    int videoStreamIndex;
};

// Setting the VideoFileConfig.nativePointer is delegated to Java part
void video_config_new(jobject instance, int fd);

VideoConfig *video_config_get(jobject jVideoConfig);

void video_config_free(jobject jVideoConfig);

#endif //WHATTHECODEC_VIDEO_CONFIG_H
