/**
 * @file VR_EngineIF.h
 * @brief interface for EngineIF.
 *
 *
 * @attention used for C++ only.
 */
#ifndef VR_ENGINEIF_H
#define VR_ENGINEIF_H

#ifndef __cplusplus
# error ERROR: This file requires C++ compilation (use a .cpp suffix)
#endif

//#include <string>

// AudioInstreamer
class VR_AudioInStreamer
{
public:
    virtual ~VR_AudioInStreamer() {}
    virtual int OnAudioInData(short* buf, size_t len) = 0;
};

/**
 * @brief The VR_EngineIF class
 *
 * class declaration
 */
class VR_EngineIF
{
public:
    virtual ~VR_EngineIF() {}

    // Session begin
    virtual VR_AudioInStreamer* StartDialog(const std::string &doMainType="") = 0;

    virtual VR_AudioInStreamer* getAudioInStreamer() = 0;
    // Session stop
    virtual int StopDialog() = 0;

    // VR service send message to engine
    virtual bool SendMessage(const std::string& message, int actionSeqId = -1) = 0;
};

// Please name the create instance, destroy instance, engine so as following:
// VR_CreateEngine()
// VR_DestroyEngine()
// libvr_engine_Alexa.so
// libvr_engine_Baidu.so
// libvr_engine_Google.so

#endif // VR_ENGINEIF_H
/* EOF */
