#ifndef VR_JNI_LISTENER_H
#define VR_JNI_LISTENER_H

#include <jni.h>
#include "VoiceRecogBaiduListener.h"

class VrBaiduJniListener : public VoiceRecogBaiduListener {
public:
    VrBaiduJniListener(JNIEnv* env, jobject thiz);
    virtual ~VrBaiduJniListener();

    virtual void OnVrServiceReplyMessage(const std::string& msgName, const std::string& msg);

private:
    virtual JNIEnv* getENV(bool& bAttached);

private:
    jclass      mClass;
    const static std::string    VR_BAIDU_SERVICE_CLASS_NAME;
    const static std::string    VR_REPLY_MSG_FUNC;
    const static std::string    FUNC_PARAM;
};

#endif