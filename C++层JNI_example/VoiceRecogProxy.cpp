#include "EV_Define.h"
#include "EV_EventSystem.h"
#include "BL_AplSystem.h"
#include "NaviEventFactory.h"
#include "VoiceRecogProxy.h"
#include "JNIHelper.h"

#include <android/log.h>
#include <boost/bind.hpp>
#include <sys/types.h>
#include <sys/stat.h>

#include "navi/Voice/VoiceRecog/VrNotifyCommonProperty.pb.h"

using namespace std;
using namespace navi::VoiceRecog;

/***********************************
 *        APK VR MODULE: 0x1000
 **********************************/
static const INT JNI_VUI_ID = 0x1000;

VoiceRecogProxy::VoiceRecogProxy()
     : m_pRecv(nullptr)
     , m_evfactory(new EV_NaviEventFactory)
    {
    __android_log_print(ANDROID_LOG_DEBUG, "VoiceRecog", "construct");
}

VoiceRecogProxy::~VoiceRecogProxy() {
    __android_log_print(ANDROID_LOG_DEBUG, "VoiceRecog", "deconstruect");
    EV_EventSystem::Instance()->Stop();
    delete m_pRecv;
    delete m_evfactory;
}

/**
 * Response to Java UI that VR Service has
 * been started
 */

void VoiceRecogProxy::onRecvMsgFromService(const EV_MSG_PTR msg) {
    __android_log_print(ANDROID_LOG_DEBUG, "VoiceRecog", "=>  callback  onRecvMsgFromService");
    if (nullptr == msg) {
        __android_log_print(ANDROID_LOG_DEBUG, "VoiceRecog", "nullptr msg");
        return;
    }

    if ("navi.VoiceRecog.VrNotifyCommonProperty" != msg->GetTypeName()) {
        __android_log_print(ANDROID_LOG_DEBUG, "VoiceRecog", "error msg:%s", msg->GetTypeName().c_str());
        return;
    }

    const VrNotifyCommonProperty* proto = static_cast<const VrNotifyCommonProperty*>(msg);
    if (nullptr == proto) {
        __android_log_print(ANDROID_LOG_DEBUG, "VoiceRecog", "dynamic convert point error");
        return;
    }

    if (proto->funccode() == VrNotifyCommonProperty_VrNotifyCommonFunc_NotifyBaiduMsgToDE) {
        __android_log_print(ANDROID_LOG_DEBUG, "VoiceRecog", "proto funcode error");
        return;
    }

    JNIHelper::sendVrMessage(JNI_VUI_ID, proto->vuicommonaction().action().c_str());
    __android_log_print(ANDROID_LOG_DEBUG, "VoiceRecog", "mesg: %s", proto->vuicommonaction().action().c_str());
}

void VoiceRecogProxy::onDisconnectService(const EV_MSG_PTR msg)
{
    __android_log_print(ANDROID_LOG_DEBUG, "VoiceRecog", "=>  callback  onDisconnectService");
    JNIHelper::sendVrMessage(JNI_VUI_ID, "vrServiceIsDisconnect");
    __android_log_print(ANDROID_LOG_DEBUG, "VoiceRecog", "=>  send ok");
}

bool VoiceRecogProxy::Initialize() {
    __android_log_print(ANDROID_LOG_DEBUG, "VoiceRecog", "Initialize start");

    EV_EventSystem* pIns = EV_EventSystem::Instance();
    if (nullptr == pIns) {
        __android_log_print(ANDROID_LOG_DEBUG, "VoiceRecog", "Error EV_EventSystem Intance");
        return false;
    }

    EV_EventConfigure evConf;
    evConf.Add(EV_PROXY_IPC, BL_PROCESSNAME_VR, true);
    evConf.Add(EV_PROXY_IPC, "vr_nativeaction", true);
    pIns->Initialize("vr_proxy", evConf);

    pIns->AddEventFactory(m_evfactory);

    pIns->Start();

    if (nullptr == m_pRecv) {
        m_pRecv = new EV_EventReceiver("vr_proxy");
    }

    RegisterEvent();

    Start();

    int res = chmod("extdata/vrservice/cache/eventsys.vr_proxy", S_IRWXU|S_IRGRP|S_IWGRP|S_IROTH|S_IWOTH);
	 __android_log_print(ANDROID_LOG_DEBUG, "VoiceRecog", "extdata/vrservice/cache/eventsys.vr_proxy");
    if (0 != res) {
        __android_log_print(ANDROID_LOG_DEBUG, "VoiceRecog","chmod error");
    }

    __android_log_print(ANDROID_LOG_DEBUG, "VoiceRecog", "Initialize complete");
    return true;
}

void VoiceRecogProxy::RegisterEvent() {
    __android_log_print(ANDROID_LOG_DEBUG, "VoiceRecog", "RegisterEvent");

    VoiceRecogProxy* pObj = this;
    m_pRecv->Subscribe(EV_EVENT_VOICERECOG_VRNOTIFYCOMMONPROPERTY,
                    boost::bind(&VoiceRecogProxy::onRecvMsgFromService, pObj, _1));
    m_pRecv->Subscribe(EV_EVENT_NAVICOMMON_CLIENTDISCONNECTED,
                    boost::bind(&VoiceRecogProxy::onDisconnectService, pObj, _1));
}

bool VoiceRecogProxy::Start() {
    // start the event system
    __android_log_print(ANDROID_LOG_DEBUG, "VoiceRecog", "start receive");

    m_pRecv->StartReceive();
    return true;
}
