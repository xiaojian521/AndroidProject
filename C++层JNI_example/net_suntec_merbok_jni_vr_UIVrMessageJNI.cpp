#include "net_suntec_merbok_jni_vr_UIVrMessageJNI.h"
#include "JNIHelper.h"
#include "VoiceRecogProxy.h"
#include <android/log.h>

static VoiceRecogProxy* vr_proxy = nullptr;

/**
 * This function was used to prepare some environment for JNI call
 * in this function, we will use reflection in Java to get some Java
 * function to implement the communcation between C++ and Java
 */
JNIEXPORT void JNICALL Java_net_suntec_merbok_jni_vr_UIVrMessageJNI_J2CAttach
(JNIEnv *e, jclass c) {
    JNIHelper::Initialize(e, c);

    if (nullptr == vr_proxy) {
        vr_proxy = new VoiceRecogProxy;
        vr_proxy->Initialize();
    }
}

/**
 * Does not do anything
 */
JNIEXPORT void JNICALL Java_net_suntec_merbok_jni_vr_UIVrMessageJNI_J2CDetach
    (JNIEnv *e, jclass c) {
    delete vr_proxy;
    return;
}
