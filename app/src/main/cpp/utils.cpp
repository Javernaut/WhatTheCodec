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

    GET_CLASS(fields.MediaFileBuilder.clazz,
              "com/javernaut/whatthecodec/domain/MediaFileBuilder", true);

    GET_ID(GetMethodID,
           fields.MediaFileBuilder.onErrorID,
           fields.MediaFileBuilder.clazz,
           "onError", "()V");

    GET_ID(GetMethodID,
           fields.MediaFileBuilder.createBasicInfoID,
           fields.MediaFileBuilder.clazz,
           "createBasicInfo", "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;I)Lcom/javernaut/whatthecodec/domain/BasicStreamInfo;");

    GET_ID(GetMethodID,
           fields.MediaFileBuilder.onMediaFileFoundID,
           fields.MediaFileBuilder.clazz,
           "onMediaFileFound", "(Ljava/lang/String;)V");

    GET_ID(GetMethodID,
           fields.MediaFileBuilder.onVideoStreamFoundID,
           fields.MediaFileBuilder.clazz,
           "onVideoStreamFound", "(Lcom/javernaut/whatthecodec/domain/BasicStreamInfo;JIIJ)V");

    GET_ID(GetMethodID,
           fields.MediaFileBuilder.onAudioStreamFoundID,
           fields.MediaFileBuilder.clazz,
           "onAudioStreamFound", "(Lcom/javernaut/whatthecodec/domain/BasicStreamInfo;JLjava/lang/String;IILjava/lang/String;)V");

    GET_ID(GetMethodID,
           fields.MediaFileBuilder.onSubtitleStreamFoundID,
           fields.MediaFileBuilder.clazz,
           "onSubtitleStreamFound", "(Lcom/javernaut/whatthecodec/domain/BasicStreamInfo;)V");

    return 0;
}

void utils_fields_free(JavaVM *vm) {
    JNIEnv *env = utils_get_env();
    if (vm == nullptr) {
        return;
    }

    env->DeleteGlobalRef(fields.MediaFileBuilder.clazz);

    javaVM = nullptr;
}

void utils_call_instance_method_void(jobject instance, jmethodID methodID, ...) {
    va_list args;
    va_start(args, methodID);
    utils_get_env()->CallVoidMethodV(instance, methodID, args);
    va_end(args);
}

jobject utils_call_instance_method_result(jobject instance, jmethodID methodID, ...) {
    va_list args;
    va_start(args, methodID);
    jobject result = utils_get_env()->CallObjectMethodV(instance, methodID, args);
    va_end(args);
    return result;
}