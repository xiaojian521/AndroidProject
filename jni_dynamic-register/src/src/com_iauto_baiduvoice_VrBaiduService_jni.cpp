/**
 * Copyright @ 2013 - 2016 Suntec Software(Shanghai) Co., Ltd.
 * All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are NOT permitted except as agreed by
 * Suntec Software(Shanghai) Co., Ltd.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

#include <cutils/log.h>
#include <jni.h>
#include <JNIHelp.h>
#include <binder/Parcel.h>
#include "android_runtime/AndroidRuntime.h"
#include "utils/Errors.h"  // for status_t
#include "jni/android_os_Parcel.h"
#include "jni/android_util_Binder.h"
#include "VrBaiduJniListener.h"
#include "VR_ModuleIF.h"
#include "vraidlservicemanager/VRAidlServiceManagerInterface.h"
#include "common/VRAidlCommon.h"
#include "vraidlservicemanager/VRAidlServiceManager.h"

#ifdef LOG_TAG
#undef LOG_TAG
#endif
using namespace merbok;
#define gVrInstance VR_ModuleIF::Instance()
#define LOG_TAG "VrBaiduServiceJni"

namespace nutshell {


static void JNI_native_setupListener(JNIEnv *env, jobject thiz) {
    ALOGD("JNI_native_setupListener");
    std::shared_ptr<VoiceRecogBaiduListener> listener(new VrBaiduJniListener(env, thiz));
    //gVrInstance->SetJniListener(listener);
    ALOGD("JNI_native_setupListener success");
}
//
static void JNI_native_onCreate(JNIEnv *env, jobject thiz) {
    ALOGD("JNI_native_onCreate");
    gVrInstance->Initialize();

    //creat stub
    createVRAidlManager();
}
static void JNI_native_onDestroy(JNIEnv *env, jobject thiz) {
    ALOGD("JNI_native_onDestroy");
    gVrInstance->Destory();
}
static void JNI_native_onStart(JNIEnv *env, jobject thiz) {
    ALOGD("JNI_native_onStart");
    gVrInstance->Start();
}
static void JNI_native_onStop(JNIEnv *env, jobject thiz) {
    ALOGD("JNI_native_onStop");
    gVrInstance->Stop();
}
static void JNI_native_onSuspend(JNIEnv *env, jobject thiz) {
    ALOGD("JNI_native_onSuspend");

}
static void JNI_native_onAwake(JNIEnv *env, jobject thiz) {
    ALOGD("JNI_native_onAwake");

}

static void JNI_native_onUIResumed(JNIEnv *env, jobject thiz) {
    ALOGD("JNI_native_onUIResumed");

}

static void JNI_native_onCommand(JNIEnv *env, jobject thiz) {
    ALOGD("JNI_native_onCommand");

}

static void JNI_native_ttsMsg(JNIEnv *env, jobject thiz,
    jstring msg) {

}

static void JNI_native_reqStart(JNIEnv *env, jobject thiz, jint AppType, jstring AppID) {

}

static void JNI_native_reqStop(JNIEnv *env, jobject thiz, jstring SessionID, jint Type) {

}

static void JNI_native_reqSendDataToVR(JNIEnv *env, jobject thiz, jstring SessionID, jstring Data) {

}

static void JNI_native_iautolinkSendDataToVR(JNIEnv *env, jobject thiz,jstring SessionID, jstring Msg, jstring DateType) {

}

static void JNI_native_iSuggestionDataToVR(JNIEnv *env, jobject thiz,jstring Msg) {

}

static void JNI_native_wechatSendDataToVR(JNIEnv *env, jobject thiz,jstring SessionID, jstring Msg) {

}

static void JNI_native_notifyUI(JNIEnv *env, jobject thiz,jstring SessionID, jstring Msg) {

}

static void JNI_native_replyStart(JNIEnv *env, jobject thiz, jint AppType, jstring SessionID, jint Result) {

}

static void JNI_native_replyStop(JNIEnv *env, jobject thiz, jstring SessionID, jint Type) {

}

static void JNI_native_replySendDataToVR(JNIEnv *env, jobject thiz, jstring SessionID, jboolean Success) {

}

static void JNI_native_highLevelInterrupt(JNIEnv *env, jobject thiz, jstring SessionID, jint InterruptType) {

}

static void JNI_native_receiveKWD(JNIEnv *env, jobject thiz, jstring Msg) {

}

static void JNI_native_notifyiAutolinkInfo(JNIEnv *env, jobject thiz,jstring SessionID, jstring Msg) {

}

static void JNI_native_notify(JNIEnv *env, jobject thiz,jstring Msg, jstring SessionID) {

}

std::string ConvertJByteaArrayToChars(JNIEnv *env, jbyteArray bytearray)
{
    jbyte *bytes = env->GetByteArrayElements(bytearray, 0);
    int length = env->GetArrayLength(bytearray);
    ALOGD("Length of byte array is: [%d]", length);

    return std::string((char*)bytes, length);
}

static JNINativeMethod sMethods[] = {

    {
        "native_setupListener",
        "()V",
        (void*)JNI_native_setupListener
    }, {
        "native_native_onCreate",
        "()V",
        (void*)JNI_native_onCreate
    }, {
        "native_onDestroy",
        "()V",
        (void*)JNI_native_onDestroy
    }, {
        "native_onStart",
        "()V",
        (void*)JNI_native_onStart
    }, {
        "native_onStop",
        "()V",
        (void*)JNI_native_onStop
    }, {
        "native_onSuspend",
        "()V",
        (void*)JNI_native_onSuspend
    }, {
        "native_onAwake",
        "()V",
        (void*)JNI_native_onAwake
    }, {
        "native_onUIResumed",
        "()V",
        (void*)JNI_native_onUIResumed
    }, {
        "native_onCommand",
        "()V",
        (void*)JNI_native_onCommand
    }, {
        "native_ttsMsg",
        "(Ljava/lang/String;)V",
        (void*)JNI_native_ttsMsg
    }, {
        "native_reqStart",
        "(ILjava/lang/String;)V",
        (void*)JNI_native_reqStart
    }, {
        "native_reqStop",
        "(Ljava/lang/String;I)V",
        (void*)JNI_native_reqStop
    }, {
        "native_reqSendDataToVR",
        "(Ljava/lang/String;Ljava/lang/String;)V",
        (void*)JNI_native_reqSendDataToVR
    }, {
        "native_iautolinkSendDataToVR",
        "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
        (void*)JNI_native_iautolinkSendDataToVR
    }, {
        "native_iSuggestionDataToVR",
        "(Ljava/lang/String;)V",
        (void*)JNI_native_iSuggestionDataToVR
    }, {
        "native_wechatSendDataToVR",
        "(Ljava/lang/String;Ljava/lang/String;)V",
        (void*)JNI_native_wechatSendDataToVR
    }, {
        "native_notifyUI",
        "(Ljava/lang/String;Ljava/lang/String;)V",
        (void*)JNI_native_notifyUI
    }, {
        "native_replyStart",
        "(ILjava/lang/String;I)V",
        (void*)JNI_native_replyStart
    }, {
        "native_replyStop",
        "(Ljava/lang/String;I)V",
        (void*)JNI_native_replyStop
    }, {
        "native_replySendDataToVR",
        "(Ljava/lang/String;Z)V",
        (void*)JNI_native_replySendDataToVR
    }, {
        "native_highLevelInterrupt",
        "(Ljava/lang/String;I)V",
        (void*)JNI_native_highLevelInterrupt
    }, {
        "native_receiveKWD",
        "(Ljava/lang/String;)V",
        (void*)JNI_native_receiveKWD
    }, {
        "native_notifyiAutolinkInfo",
        "(Ljava/lang/String;Ljava/lang/String;)V",
        (void*)JNI_native_notifyiAutolinkInfo
    }, {
        "native_notify",
        "((Ljava/lang/String;Ljava/lang/String;)V",
        (void*)JNI_native_notify
    }
};

// This function only registers the native methods
static int register_VrBaiduServiceJni(JNIEnv *env) {
    return android::AndroidRuntime::registerNativeMethods(env,
        "com/iauto/baiduvoice/VrBaiduServiceJni", sMethods, NELEM(sMethods));
}

}  // namespace nutshell


JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
    ALOGD("VrBaiduService : loading JNI");
    JNIEnv* env = NULL;

    if (vm->GetEnv((void**) &env, JNI_VERSION_1_6) != JNI_OK) {
        ALOGE("JNI version mismatch error");
        return JNI_ERR;
    }

    int status = nutshell::register_VrBaiduServiceJni(env);
    if (status < 0) {
      ALOGE("jni adapter service registration failure, status: %d", status);
      return JNI_ERR;
    }

    ALOGD("VrBaiduService : loading JNI success");
    /* success -- return valid version number */
    return JNI_VERSION_1_6;
}

