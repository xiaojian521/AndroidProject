/**
 * @file VR_EngineListenerIF.h
 * @brief interface for EngineListenerIF.
 *
 *
 * @attention used for C++ only.
 */
#ifndef VR_ENGINE_LISTENER_H
#define VR_ENGINE_LISTENER_H

#ifndef __cplusplus
# error ERROR: This file requires C++ compilation (use a .cpp suffix)
#endif

#include <string>

// VR_DialogState
enum class VR_DialogState{
    IDLE = 0,
    LISTENING,
    THINKING,
    SPEAKING,
    INVALIDE
};


/**
 * @brief The VR_EngineListenerIF interface
 *
 * interface declaration
 */
class VR_EngineListenerIF
{
public:
    virtual ~VR_EngineListenerIF() {}
    virtual void OnDialogStateChanged(VR_DialogState state) = 0;

    // VR Service receive message from engine
    virtual void OnReceiveMessage(const std::string& message, int seqId = -1) = 0;
};

#endif  // VR_ENGINE_LISTENER_H
/* EOF */

