//
// Created by Alex Javernaut on 3/25/19.
//

#ifndef WHATTHECODEC_UTILS_H
#define WHATTHECODEC_UTILS_H

#include <jni.h>
#include "log.h"

int utils_fields_init(JavaVM *vm);

void utils_fields_free(JavaVM *vm);

// The approach was taken from here:
// https://code.videolan.org/videolan/vlc-android/blob/master/libvlc/jni/utils.h
// https://code.videolan.org/videolan/vlc-android/blob/master/libvlc/jni/libvlcjni.c

struct {
    struct {
        jclass clazz;
        jfieldID nativePointer;
    } VideoFileConfig;
} fields;

#endif //WHATTHECODEC_UTILS_H
