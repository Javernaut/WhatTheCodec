//
// Created by Alex Javernaut on 3/25/19.
//

#ifndef WHATTHECODEC_UTILS_H
#define WHATTHECODEC_UTILS_H

#include <jni.h>

/**
 * Initializes the fields struct that keeps handles to VideoFileConfig's internal fields in order to read/write to them.
 */
int utils_fields_init(JavaVM *vm);

/**
 * Frees resources created in utils_fields_init(JavaVM *vm).
 */
void utils_fields_free(JavaVM *vm);

/**
 * Returns a pointer to JNIEnv struct to use in various JNI-specific functions.
 */
JNIEnv *utils_get_env();

// The approach was taken from here:
// https://code.videolan.org/videolan/vlc-android/blob/master/libvlc/jni/utils.h
// https://code.videolan.org/videolan/vlc-android/blob/master/libvlc/jni/libvlcjni.c

struct fields {
    struct {
        jclass clazz;
        jfieldID nativePointer;
    } VideoFileConfig;
};

extern struct fields fields;

#endif //WHATTHECODEC_UTILS_H
