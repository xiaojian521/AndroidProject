/**
 * Copyright @ 2015 - 2016 Suntec Software(Shanghai) Co., Ltd.
 * All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are NOT permitted except as agreed by
 * Suntec Software(Shanghai) Co., Ltd.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

/**
 * @file VR_EngineFactory.h
 * @brief interface for Engine Factory.
 *
 *
 * @attention used for C++ only.
 */
#ifndef VR_ENGINEFACTORY_H
#define VR_ENGINEFACTORY_H

#ifndef __cplusplus
# error ERROR: This file requires C++ compilation (use a .cpp suffix)
#endif

#include <string>

class VR_EngineIF;
class VR_EngineListenerIF;

/**
 * @brief The VR_EngineFactory class
 *
 * class declaration
 */
class VR_EngineFactory
{
public:
    static bool LoadEngineLibrary(const std::string& strEngineType);
    static VR_EngineIF* CreateEngineInstance(const std::string& strEngineType, VR_EngineListenerIF* listener);
    static bool DestroyEngineInstance(const std::string& strEngineType, VR_EngineIF* engine = nullptr);
};

#endif // VR_ENGINEFACTORY_H
/* EOF */
