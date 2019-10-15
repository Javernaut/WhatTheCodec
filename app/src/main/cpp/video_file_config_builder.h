//
// Created by Alexander Berezhnoi on 14/10/19.
//

#ifndef WHATTHECODEC_VIDEO_FILE_CONFIG_BUILDER_H
#define WHATTHECODEC_VIDEO_FILE_CONFIG_BUILDER_H

#include <jni.h>

/**
 * Creates an AVFormatContext struct according to the parameter. Notifies a VideoFileConfigBuilder
 * object about each media stream found and passes all its metadata.
 *
 * @param jVideoFileConfigBuilder a VideoFileConfigBuilder java object
 * @param fd a file descriptor to a local file
 */
void video_file_config_build(jobject jVideoFileConfigBuilder, int fd);

/**
 * Creates an AVFormatContext struct according to the parameter. Notifies a VideoFileConfigBuilder
 * object about each media stream found and passes all its metadata.
 *
 * @param jVideoFileConfigBuilder a VideoFileConfigBuilder java object
 * @param filePath a file path to a local file
 */
void video_file_config_build(jobject jVideoFileConfigBuilder, const char *filePath);

#endif //WHATTHECODEC_VIDEO_FILE_CONFIG_BUILDER_H
