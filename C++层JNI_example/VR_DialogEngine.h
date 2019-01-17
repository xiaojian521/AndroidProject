#ifndef VR_DIALOGENGINE
#define VR_DIALOGENGINE

#include "VR_EngineListenerIF.h"
#include "VR_EngineIF.h"
#include "VR_EngineFactory.h"
#include <atomic>
#include <android/log.h>
 
class VR_DialogEngine
{
public:
    ~VR_DialogEngine();

    bool CreateEngine();
    void SendMessage(const std::string& msg);
    void StopDiag();
    void StartDiag(const std::string &doMainType = "");
    void WriteAudioDataToEngine(short *pData, size_t size);

    static VR_DialogEngine* getInstance();
private:
    VR_DialogEngine();
    VR_DialogEngine(const VR_DialogEngine&);
    VR_DialogEngine& operator = (const VR_DialogEngine&);

private:
    VR_EngineIF* m_pEngine;
    VR_EngineListenerIF* m_pEngineListener;
    VR_AudioInStreamer* m_pAudioInStreamer;
    std::atomic<int> m_testSeq;
};

#endif // VR_DIALOGENGINE

