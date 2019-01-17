#include "MEM_MemoryAllocateConfig.h"
#include "net_suntec_merbok_jni_vr_UIVrControlJNI.h"

#include "navi/Voice/VoiceRecog/VrReqStarVoiceRecog.pb.h"
#include "navi/Voice/VoiceRecog/VrReqCancelVoiceRecog.pb.h"
#include "navi/Voice/VoiceRecog/VrReqGetServiceAlivingStatus.pb.h"
#include "navi/Voice/VoiceRecog/RequestVrCommonProperty.pb.h"
#include "navi/Voice/VoiceRecog/VrReqCommonAlexaMsg.pb.h"

#include "EV_Define.h"
#include "EV_EventSender.h"

#include <android/log.h>

using namespace navi::VoiceRecog;

/**
 * This function will be called by Java UI when user push to talk(PTT)
 * Inner this function, we will send a event EV_XXX to VR Service to
 * wakeup it to ready to recognize the voice that user will be talk
 */
JNIEXPORT void JNICALL Java_net_suntec_merbok_jni_vr_UIVrControlJNI_StartVR
    (JNIEnv *env, jclass o) {
    __android_log_print(ANDROID_LOG_DEBUG, "VoiceRecog", "Call start VR");

    std::unique_ptr<VrReqStarVoiceRecog> spMsg(VrReqStarVoiceRecog::default_instance().New());

    EV_EventSender cSender;
    cSender.SendEvent(EV_EVENT_VOICERECOG_REQSTARTVOICERECOG, spMsg.release());
}

/**
 * This function will be called by Java UI when it want to fetch the
 * inner status of VR Service, this value lists as follows:
 * IDLE:
 * LISTENING:
 * PROCESSING:
 * PLAYING:
 */
JNIEXPORT jint JNICALL Java_net_suntec_merbok_jni_vr_UIVrControlJNI_GetCurrentVrStatus
    (JNIEnv *env, jclass o) {
    __android_log_print(ANDROID_LOG_DEBUG, "VoiceRecog", "Get status of VR service");

    std::unique_ptr<VrReqGetServiceAlivingStatus> spMsg(VrReqGetServiceAlivingStatus::default_instance().New());

    EV_EventSender cSender;
    cSender.SendEvent(EV_EVENT_VOICERECOG_REQGETSERVICEALIVINGSTATUS, spMsg.release());
    return 0;
}

JNIEXPORT void JNICALL Java_net_suntec_merbok_jni_vr_UIVrControlJNI_ResetVr
    (JNIEnv *env, jclass o) {
    __android_log_print(ANDROID_LOG_DEBUG, "VoiceRecog", "resetVr");

    std::unique_ptr<VrReqCommonAlexaMsg> spMsg(VrReqCommonAlexaMsg::default_instance().New());
    spMsg->set_data("resetAlexa");

    EV_EventSender cSender;
    cSender.SendEvent(EV_EVENT_VOICERECOG_REQCOMMONALEXAMSG, spMsg.release());
}

JNIEXPORT void JNICALL Java_net_suntec_merbok_jni_vr_UIVrControlJNI_SetUseVr
    (JNIEnv *env, jclass o, jboolean status) {
    __android_log_print(ANDROID_LOG_DEBUG, "VoiceRecog", "SetUseVr");

    std::string content;
    bool flag = status;

    if (flag) {
        content = "openAlexa";
    }
    else {
        content = "closeAlexa";
    }

    std::unique_ptr<VrReqCommonAlexaMsg> spMsg(VrReqCommonAlexaMsg::default_instance().New());
    spMsg->set_data(content);

    EV_EventSender cSender;
    cSender.SendEvent(EV_EVENT_VOICERECOG_REQCOMMONALEXAMSG, spMsg.release());
}

JNIEXPORT void JNICALL Java_net_suntec_merbok_jni_vr_UIVrControlJNI_SetEngineUseVr
    (JNIEnv *env, jclass o, jstring status) {
    const char* _content = env->GetStringUTFChars(status, 0);
    __android_log_print(ANDROID_LOG_DEBUG, "VoiceRecog", "SetEngine %s", _content);

    std::unique_ptr<VrReqCommonAlexaMsg> spMsg(VrReqCommonAlexaMsg::default_instance().New());
    spMsg->set_data(_content);

    EV_EventSender cSender;
    cSender.SendEvent(EV_EVENT_VOICERECOG_REQCOMMONALEXAMSG, spMsg.release());
}

/**
 * This function will be called when user cancel the current progress of
 * recognizing or the full progress has been finished. At this time, Java
 * UI told VR Service that just reset all status, clean some useless
 * memory and wait for next recognizing
 */
JNIEXPORT void JNICALL Java_net_suntec_merbok_jni_vr_UIVrControlJNI_ExitVR
    (JNIEnv *env, jclass o) {
    __android_log_print(ANDROID_LOG_DEBUG, "VoiceRecog", "Exit VR");

    std::unique_ptr<VrReqCancelVoiceRecog> spMsg(VrReqCancelVoiceRecog::default_instance().New());

    EV_EventSender cSender;
    cSender.SendEvent(EV_EVENT_VOICERECOG_REQCANCELVOICERECOG, spMsg.release());
}

/**
 * This function is an empty implementation, currently no caller will use it
 * we return an invalid value(-1) to this caller in case of some wrong calling
 */
JNIEXPORT jint JNICALL Java_net_suntec_merbok_jni_vr_UIVrControlJNI_GetCurrentVolume
    (JNIEnv *env, jclass o) {
    return -1;
}

/**
 * This function is an empty implementation, currently no caller will use it
 */
JNIEXPORT jstring JNICALL Java_net_suntec_merbok_jni_vr_UIVrControlJNI_getContentResult
(JNIEnv *env, jclass o) {
    return env->NewStringUTF("");
}
