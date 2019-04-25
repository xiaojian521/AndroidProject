#include "VrBaiduJniListener.h"
#include <cutils/log.h>
#include <android_runtime/AndroidRuntime.h>

#ifdef LOG_TAG
#undef LOG_TAG
#endif

#define LOG_TAG "VrBaiduServiceJni"

const std::string VrBaiduJniListener::VR_BAIDU_SERVICE_CLASS_NAME = "com/iauto/vr/VrBaiduServiceJni";
const std::string VrBaiduJniListener::VR_REPLY_MSG_FUNC = "onVrServiceMessage";
const std::string VrBaiduJniListener::FUNC_PARAM = "(Ljava/lang/String;[B)V";

static jmethodID sMethodOnVrMessage;
static JavaVM *g_jvm = NULL;

VrBaiduJniListener::VrBaiduJniListener(JNIEnv* env, jobject thiz)
{
    jclass claz = env->FindClass(VR_BAIDU_SERVICE_CLASS_NAME.c_str());

    if (claz == NULL) {
        ALOGD("JNI_native_init: can't find VrBaiduServiceProxy class");
        return;
    }

    mClass = (jclass)env->NewGlobalRef(claz);
    env->GetJavaVM(&g_jvm);


    sMethodOnVrMessage = env->GetStaticMethodID(mClass, VR_REPLY_MSG_FUNC.c_str(), FUNC_PARAM.c_str());
    if (sMethodOnVrMessage == NULL) {
        ALOGD("JNI_native_init: can't find method onVrServiceMessage");
        return;
    }
}

VrBaiduJniListener::~VrBaiduJniListener() {
    JNIEnv *env = android::AndroidRuntime::getJNIEnv();
    env->DeleteGlobalRef(mClass);

}

JNIEnv* VrBaiduJniListener::getENV(bool& bAttached) {
    bAttached = false;
    JNIEnv *envnow = NULL;
    
    int status = g_jvm->GetEnv((void **)&envnow, JNI_VERSION_1_6);
    if (status < 0) {
        status = g_jvm->AttachCurrentThread(&envnow, NULL);
        if(status < 0)
        {
            return NULL;
        }
        bAttached = true;
    }

    return envnow;

}

void VrBaiduJniListener::OnVrServiceReplyMessage(const std::string& msgName, const std::string& msg)
{
    ALOGD("VrBaiduJniListener::OnVrServiceReplyMessage - msgName: %s", msgName.c_str());

    bool bAttached = false;
    JNIEnv *env = getENV(bAttached);
    
    if (NULL == env) {
        ALOGD("VrBaiduJniListener get NULL env");
        return;
    }

    jstring jName = env->NewStringUTF(msgName.data());
    jbyteArray byteArray = env->NewByteArray(msg.length());
    env->SetByteArrayRegion(byteArray, 0, msg.length(), (jbyte*)msg.data());

    env->CallStaticVoidMethod(mClass, sMethodOnVrMessage, jName, byteArray);
    ALOGD("VrBaiduJniListener:: length of msg [%s] is: [%d]", msgName.c_str(), msg.length());

    if (bAttached) {
        g_jvm->DetachCurrentThread();
    }
}
