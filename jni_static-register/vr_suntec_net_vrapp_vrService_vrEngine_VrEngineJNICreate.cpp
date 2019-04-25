#include "vr_suntec_net_vrapp_vrService_vrEngine_VrEngineJNICreate.h"
#include <android/log.h>
#include "JNIHelper.h"
#include "VR_DialogEngine.h"

static VR_DialogEngine* pDialogEngine = nullptr;

JNIEXPORT jboolean JNICALL Java_vr_suntec_net_vrapp_vrService_vrEngine_VrEngineJNICreate_CreateEngine
(JNIEnv *e, jclass c)
{
    __android_log_print(ANDROID_LOG_DEBUG, "VrJNI", "CreateEngine");
    if (!JNIHelper::Initialize(e, c)) {
        __android_log_print(ANDROID_LOG_DEBUG, "VrJNI", "JNIHelper::Initialize fail");
        return false;
    }

    pDialogEngine = VR_DialogEngine::getInstance();
    if (pDialogEngine != nullptr) {
        if (pDialogEngine->CreateEngine()) {
            __android_log_print(ANDROID_LOG_DEBUG, "VrJNI", "CreateEngine sucess");
            return true;
        }
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_vr_suntec_net_vrapp_vrService_vrEngine_VrEngineJNICreate_DestroyEngine(JNIEnv *, jclass)
{
    if(pDialogEngine != nullptr) {
        delete pDialogEngine;
        pDialogEngine = nullptr;
        __android_log_print(ANDROID_LOG_DEBUG, "VrJNI", "DestroyEngine sucess");
    }
    return true;
}