//
// Created by Alexander Berezhnoi on 14/10/19.
//

#ifndef WHATTHECODEC_MEDIA_FILE_BUILDER_H
#define WHATTHECODEC_MEDIA_FILE_BUILDER_H

#include <jni.h>

/**
 * Creates an AVFormatContext struct according to the parameter. Notifies a MediaFileBuilder
 * object about each media stream found and passes all its metadata.
 *
 * @param jMediaFileBuilder a MediaFileBuilder java object
 * @param fileDescriptor a file descriptor to a local file
 * @param mediaStreamsMask a bit set of media streams to extract from the file
 */
void media_file_build(jobject jMediaFileBuilder, int fileDescriptor, int mediaStreamsMask);

/**
 * Creates an AVFormatContext struct according to the parameter. Notifies a MediaFileBuilder
 * object about each media stream found and passes all its metadata.
 *
 * @param jMediaFileBuilder a MediaFileBuilder java object
 * @param assetFileDescriptor a file descriptor to a file in Assets
 * @param startOffset an amount of bytes to skip before actual data reading
 * @param shortFormatName the name of the media container, as there is a problem probing it this case
 * @param mediaStreamsMask a bit set of media streams to extract from the file
 */
void media_file_build(jobject jMediaFileBuilder, int assetFileDescriptor, int64_t startOffset, const char *shortFormatName, int mediaStreamsMask);

/**
 * Creates an AVFormatContext struct according to the parameter. Notifies a MediaFileBuilder
 * object about each media stream found and passes all its metadata.
 *
 * @param jMediaFileBuilder a MediaFileBuilder java object
 * @param filePath a file path to a local file
 * @param mediaStreamsMask a bit set of media streams to extract from the file
 */
void media_file_build(jobject jMediaFileBuilder, const char *filePath, int mediaStreamsMask);

#endif //WHATTHECODEC_MEDIA_FILE_BUILDER_H
