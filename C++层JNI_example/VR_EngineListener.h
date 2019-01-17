#ifndef VR_ENGINELISTENER
#define VR_ENGINELISTENER

#endif // VR_ENGINELISTENER
#include "VR_EngineListenerIF.h"
#include <android/log.h>

class VR_EngineListener : public VR_EngineListenerIF
{
public:
    virtual ~VR_EngineListener() {}
    virtual void OnDialogStateChanged(VR_DialogState state);
    virtual void OnReceiveMessage(const std::string& message, int seqId = -1);
};
