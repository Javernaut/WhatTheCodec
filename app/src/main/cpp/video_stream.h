//
// Created by Alexander Berezhnoi on 14/10/19.
//

#ifndef WHATTHECODEC_VIDEO_STREAM_H
#define WHATTHECODEC_VIDEO_STREAM_H

#include <jni.h>

extern "C" {
#include <libavformat/avformat.h>
#include <libavcodec/avcodec.h>
}

/**
 * A struct that is stored in a VideoStream.nativePointer.
 * Aggregates necessary pointers to FFmpeg structs.
 */
struct VideoStream {
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
 * Function returns a pointer to VideoStream struct that is stored in jVideoStream.nativePointer.
 *
 * @param jVideoStream a VideoStream java object
 */
VideoStream *video_stream_get(jobject jVideoStream);

/**
 * Converts a pointer to VideoStream struct to a long to be stored in JVM part.
 *
 * @param videoStream a pointer to convert
 * @return a converted pointer
 */
long video_stream_get_handle(VideoStream *videoStream);

/**
 * Frees the VideoStream struct that is stored in jVideoStream.nativePointer. Sets this field to -1.
 *
 * @param jVideoStream a VideoStream java object
 */
void video_stream_free(jobject jVideoStream);

#endif //WHATTHECODEC_VIDEO_STREAM_H
