//
// Created by Alex Javernaut on 3/24/19.
//

#ifndef WHATTHECODEC_VIDEO_CONFIG_H
#define WHATTHECODEC_VIDEO_CONFIG_H

#include <jni.h>

extern "C" {
#include <libavformat/avformat.h>
#include <libavcodec/avcodec.h>
}

/**
 * A struct that is stored in VideoFileConfig.nativePointer.
 * Aggregates necessary pointers to FFmpeg structs.
 */
struct VideoConfig {
    // True if a config is created for file path and False for file descriptor
    bool fullFeatured;
    // Actual file descriptor. If the config is created for path, then the field isn't set at all.
    int fileDescriptor;
    // Root FFmpeg object for the given media.
    AVFormatContext *avFormatContext;
    // Parameters of a video stream.
    AVCodecParameters *parameters;
    // Codec of a video stream.
    AVCodec *avVideoCodec;
    // And index of a video stream in the avFormatContext.
    int videoStreamIndex;
};

/**
 * Creates an AVFormatContext struct according to the parameter. If all data is read successfully,
 * a VideoConfig struct is created and attached to the jVideoConfig object.
 *
 * @param jVideoConfig a VideoFileConfig java object.
 * @param fd a file descriptor to a local file. It will be closed with close(fd) in video_config_free(jVideoConfig).
 */
void video_config_new(jobject jVideoConfig, int fd);

/**
 * Creates a AVFormatContext struct according to the parameter. If all data is read successfully,
 * a VideoConfig struct is created and attached to the jVideoConfig object.
 *
 * @param jVideoConfig a VideoFileConfig java object.
 * @param filePath a file path to a local file.
 */
void video_config_new(jobject jVideoConfig, const char *filePath);

/**
 * Function returns a pointer to VideoConfig struct that is stored in jVideoConfig.nativePointer.
 *
 * @param jVideoConfig a VideoFileConfig java object.
 */
VideoConfig *video_config_get(jobject jVideoConfig);

/**
 * Frees the VideoConfig struct that is stored in jVideoConfig.nativePointer. Sets this field to -1.
 *
 * @param jVideoConfig a VideoFileConfig java object.
 */
void video_config_free(jobject jVideoConfig);

#endif //WHATTHECODEC_VIDEO_CONFIG_H
