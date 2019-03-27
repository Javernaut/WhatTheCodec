//
// Created by Alex Javernaut on 3/25/19.
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

    GET_CLASS(fields.VideoFileConfig.clazz,
              "com/javernaut/whatthecodec/VideoFileConfig", true);

    GET_ID(GetFieldID,
           fields.VideoFileConfig.nativePointer,
           fields.VideoFileConfig.clazz,
           "nativePointer", "J");

    return 0;
}

void utils_fields_free(JavaVM *vm) {
    JNIEnv *env = utils_get_env();
    if (vm == nullptr) {
        return;
    }

    env->DeleteGlobalRef(fields.VideoFileConfig.clazz);

    javaVM = nullptr;
}