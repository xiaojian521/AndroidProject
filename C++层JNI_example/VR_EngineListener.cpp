#include "VR_EngineListener.h"
#include "JNIHelper.h"
#include <android/log.h>
void VR_EngineListener::OnDialogStateChanged(VR_DialogState state)
{
    printf("OnDialogStateChanged state = %d\n", state);
}

void VR_EngineListener::OnReceiveMessage(const std::string &message, int seqId)
{
    printf("OnReceiveMessage message = %s, seqId = %d\n", message.c_str(), seqId);
    __android_log_print(ANDROID_LOG_DEBUG, "VrJNI", "OnReceiveMessage=%s" , message.c_str());
    JNIHelper::CallBackVrMsg(message);
}

