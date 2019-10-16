//
// Created by Alexander Berezhnoi on 25/03/19.
//

#include "utils.h"
#include "log.h"

struct fields fields;
static JavaVM *javaVM;

JNIEnv *utils_get_env() {
    JNIEnv *env;
    if (javaVM->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return nullptr;
    }
    return env;
}

int utils_fields_init(JavaVM *vm) {
    javaVM = vm;

    JNIEnv *env = utils_get_env();
    if (env == nullptr) {
        return -1;
    }

#define GET_CLASS(clazz, str, b_globlal) do { \
    (clazz) = env->FindClass((str)); \
    if (!(clazz)) { \
        LOGE("FindClass(%s) failed", (str)); \
        return -1; \
    } \
    if (b_globlal) { \
        (clazz) = (jclass) env->NewGlobalRef((clazz)); \
        if (!(clazz)) { \
            LOGE("NewGlobalRef(%s) failed", (str)); \
            return -1; \
        } \
    } \
} while (0)

#define GET_ID(get, id, clazz, str, args) do { \
    (id) = env->get((clazz), (str), (args)); \
    if (!(id)) { \
        LOGE(#get"(%s) failed", (str)); \
        return -1; \
    } \
} while (0)

    // Actual work

    GET_CLASS(fields.VideoStream.clazz,
              "com/javernaut/whatthecodec/domain/VideoStream", true);

    GET_ID(GetFieldID,
           fields.VideoStream.nativePointer,
           fields.VideoStream.clazz,
           "nativePointer", "J");
    GET_ID(GetFieldID,
           fields.VideoStream.fullFeatured,
           fields.VideoStream.clazz,
           "fullFeatured", "Z");

    GET_CLASS(fields.VideoFileConfigBuilder.clazz,
              "com/javernaut/whatthecodec/domain/VideoFileConfigBuilder", true);

    GET_ID(GetMethodID,
           fields.VideoFileConfigBuilder.onErrorID,
           fields.VideoFileConfigBuilder.clazz,
           "onError", "()V");

    GET_ID(GetMethodID,
           fields.VideoFileConfigBuilder.onVideoConfigFoundID,
           fields.VideoFileConfigBuilder.clazz,
           "onVideoConfigFound", "(Ljava/lang/String;)V");

    GET_ID(GetMethodID,
           fields.VideoFileConfigBuilder.onVideoStreamFoundID,
           fields.VideoFileConfigBuilder.clazz,
           "onVideoStreamFound", "(IILjava/lang/String;J)V");

    GET_ID(GetMethodID,
           fields.VideoFileConfigBuilder.onAudioStreamFoundID,
           fields.VideoFileConfigBuilder.clazz,
           "onAudioStreamFound", "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;JLjava/lang/String;IILjava/lang/String;I)V");

    return 0;
}

void utils_fields_free(JavaVM *vm) {
    JNIEnv *env = utils_get_env();
    if (vm == nullptr) {
        return;
    }

    env->DeleteGlobalRef(fields.VideoStream.clazz);
    env->DeleteGlobalRef(fields.VideoFileConfigBuilder.clazz);

    javaVM = nullptr;
}

void utils_call_instance_method(jobject instance, jmethodID methodID, ...) {
    va_list args;
    va_start(args, methodID);
    utils_get_env()->CallVoidMethodV(instance, methodID, args);
    va_end(args);
}