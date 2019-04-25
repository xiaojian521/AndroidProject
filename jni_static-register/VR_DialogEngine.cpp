#include "VR_DialogEngine.h"
#include "VR_EngineListener.h"

static VR_DialogEngine* m_pInstance = nullptr;

VR_DialogEngine::VR_DialogEngine()
    : m_pEngine(nullptr)
    , m_pEngineListener(nullptr)
    , m_pAudioInStreamer(nullptr)
    , m_testSeq(0)
{
}

VR_DialogEngine::~VR_DialogEngine()
{
    VR_EngineFactory::DestroyEngineInstance("Baidu_POC", m_pEngine);
}

VR_DialogEngine* VR_DialogEngine::getInstance()
{
    if (m_pInstance == nullptr) {
        m_pInstance = new VR_DialogEngine();
    }

    return m_pInstance;
}

bool VR_DialogEngine::CreateEngine()
{
    if (!VR_EngineFactory::LoadEngineLibrary("Baidu_POC")) {
        // printf("LoadEngineLibrary fail\n");
        
         __android_log_print(ANDROID_LOG_DEBUG, "VrJNI", "LoadEngineLibrary fail");
	return false;
    }

    m_pEngineListener = new VR_EngineListener();
    m_pEngine = VR_EngineFactory::CreateEngineInstance("Baidu_POC", m_pEngineListener);
    if (m_pEngine == nullptr) {
         __android_log_print(ANDROID_LOG_DEBUG, "VrJNI", "CreateEngine failed!");
	return false;
    }
	return true;
}

void VR_DialogEngine::WriteAudioDataToEngine(short *pData, size_t size)
{
     __android_log_print(ANDROID_LOG_DEBUG, "VrApp", "WriteAudioDataToEngine");
    if (m_pAudioInStreamer != nullptr) {

//        m_pAudioInStreamer->OnAudioInData();
    }
}
 
void VR_DialogEngine::StartDiag(const std::string &doMainType)
{
     __android_log_print(ANDROID_LOG_DEBUG, "VrJNI", "StartDiag");
    if (m_pEngine != nullptr) {
       m_pAudioInStreamer = m_pEngine->StartDialog("");
       if(m_pAudioInStreamer != nullptr ) {
       }
    }
}

void VR_DialogEngine::StopDiag()
{
     __android_log_print(ANDROID_LOG_DEBUG, "VrJNI", "StopDiag");
    if (m_pEngine != nullptr) {
        m_pEngine->StopDialog();
    }
}

void VR_DialogEngine::SendMessage(const std::string &msg)
{
     __android_log_print(ANDROID_LOG_DEBUG, "VrJNI", "SendMessage=%s", msg.c_str());
    if (m_pEngine != nullptr) {
        m_pEngine->SendMessage(msg, ++m_testSeq);
    }

    m_pEngineListener->OnReceiveMessage("hello world");
}



