#include "JNIHelper.h"
#include <android/log.h>

JavaVM*     JNIHelper::m_pJvm = nullptr;
jclass      JNIHelper::m_pClass = nullptr;
jmethodID   JNIHelper::m_pMethod = 0;
const std::string JNIHelper::METHOD_NAME = "ListenerVrMessage";
const std::string JNIHelper::METHOD_TAG = "(Ljava/lang/String;)Z";


bool JNIHelper::Initialize(JNIEnv* e, jclass c) {
    __android_log_print(ANDROID_LOG_DEBUG, "VrJNI", "JNIHelper::Initialize");
    e->GetJavaVM(&m_pJvm);
    m_pClass = (jclass)(e->NewGlobalRef(c));
    m_pMethod = e->GetStaticMethodID(c, METHOD_NAME.c_str(), METHOD_TAG.c_str());

    return true;
}

void JNIHelper::CallBackVrMsg(const std::string& name) {

    __android_log_print(ANDROID_LOG_DEBUG, "VrJNI", "JNIHelper::CallBackVrMsg");

    JNIEnv* env = nullptr;
    m_pJvm->AttachCurrentThread(&env, 0);

    jstring sName = env->NewStringUTF(name.c_str());
    if (env) {
        env->CallStaticBooleanMethod(m_pClass, m_pMethod, sName);
    }
}
