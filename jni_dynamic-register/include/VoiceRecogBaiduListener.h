/*
 * Copyright @ 2012 - 2013 Suntec Software(Shanghai) Co., Ltd.
 * All Rights Reserved.
 */
/**
 * Controler Listener Interface of Voice Recognization
 *
 * Controler Listener Interface of Voice Recognization
 *
 */
/** @file
 * VoiceRecogBaiduListener
 *
 */

#ifndef CXX_VOICERECOGBAIDULISTENER_H_
#define CXX_VOICERECOGBAIDULISTENER_H_

#ifndef __cplusplus
#   error ERROR: This file requires C++ compilation (use a .cpp suffix)
#endif
#include <string>

class VoiceRecogBaiduListener
{
public:
    virtual ~VoiceRecogBaiduListener() {}
    virtual void OnVrServiceReplyMessage(const std::string& msgName, const std::string& data) = 0;
};

#endif

