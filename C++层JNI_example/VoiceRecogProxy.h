#ifndef VOICE_RECOG_PROXY_H
#define VOICE_RECOG_PROXY_H

#include "EV_EventReceiver.h"
#include "NaviEventFactory.h"

class VoiceRecogProxy
{
public:
    VoiceRecogProxy();
    ~VoiceRecogProxy();

    bool Initialize();

    void onRecvMsgFromService(const EV_MSG_PTR msg);
    void onDisconnectService(const EV_MSG_PTR msg);

private:
    /*** this function will be used to start event system
     * and start the receiver thread to accept events
     * ends from VR Service
     */
    bool Start();

    void RegisterEvent();

    EV_EventReceiver* m_pRecv;
    EV_NaviEventFactory* m_evfactory;
};

#endif
