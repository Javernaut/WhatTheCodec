#include <jni.h>

#include "utils.h"

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    if (utils_fields_init(vm) != 0) {
        return -1;
    }
    return JNI_VERSION_1_6;
}

void JNI_OnUnload(JavaVM *vm, void *reserved) {
    utils_fields_free(vm);
}