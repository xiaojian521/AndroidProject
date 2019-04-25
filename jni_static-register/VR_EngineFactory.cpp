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

#include <dlfcn.h>
#include <string>
#include <fstream>
#include <dirent.h>
#include <map>
#include "VR_EngineFactory.h"
#include "VR_EngineIF.h"
#include <android/log.h>


#define VR_CREATE_BAIDU_POC_ENGINE    "createVRBaiduPocEng"     // "VR_CreateEngine"
#define VR_DESTROY_BAIDU_POC_ENGINE   "destoryVRBaiduPocEng"    // "VR_DestroyEngine"

using namespace std;

typedef VR_EngineIF* (*fnCreateBaiduPocEngine)(VR_EngineListenerIF*);
typedef VR_EngineIF* (*fnCreateOtherEngine)(VR_EngineListenerIF*);
typedef void (*fnDestroyEngine)();
typedef void (*fnDestroyBaiduPocEngine)(VR_EngineIF*);

const std::map<std::string, std::string>::value_type init_value[] =
{
    map<std::string, std::string>::value_type("Baidu_POC", "libvr_engine_BaiduPoc.so")
};

const static std::map<std::string, std::string> VR_EngineLibraryNameMap(init_value, init_value + 1);
static map<std::string, void*> VR_EngineLibraryHandleMap;

static void* VR_LoadLibrary(const std::string& strLibraryName)
{
     __android_log_print(ANDROID_LOG_DEBUG, "VrJNI", "VR_LoadLibrary");
    if (strLibraryName.empty()) {
        {
            static bool errorLogMark = false;
            if (!errorLogMark) {
                errorLogMark = true;
            }
        }
        return nullptr;
    }

    void* handle = nullptr;
    // std::string strlib = "/home/xiao/test/BaiduJni/libs/" + strLibraryName;
    handle = dlopen(strLibraryName.c_str(), RTLD_LAZY | RTLD_GLOBAL);

    if (!handle) {
        __android_log_print(ANDROID_LOG_DEBUG, "VrJNI", "Load engine library failed, strLibraryName=%s, errmsg= %s" , strLibraryName.c_str(), dlerror());
        return nullptr;
    }

    __android_log_print(ANDROID_LOG_DEBUG, "VrJNI", "Load engine library %s successfully !" , strLibraryName.c_str());
    return handle;
}

static VR_EngineIF* VR_LoadEngineInstance(const std::string& strEngineType, VR_EngineListenerIF* listener)
{
    __android_log_print(ANDROID_LOG_DEBUG, "VrJNI", "VR_LoadEngineInstance");
    map<std::string, void *>::iterator it;
    //xj
    it = VR_EngineLibraryHandleMap.find(strEngineType);
    if (it == VR_EngineLibraryHandleMap.end()) {
        {
            __android_log_print(ANDROID_LOG_DEBUG, "VrJNI", "VR_LoadEngineInstance");
            static bool errorLogMark = false;
            if (!errorLogMark) {
                errorLogMark = true;
                __android_log_print(ANDROID_LOG_DEBUG, "VrJNI", "VR_EngineLibraryHandleMap is empty");
            }
        }
        __android_log_print(ANDROID_LOG_DEBUG, "VrJNI", "VR_EngineLibraryHandleMap can't find");
        return nullptr;
    }
    //void* handle = dlopen("libs/libvr_engine_BiduPoc.so",RTLD_NOW);

    void* handle = it->second;
    if (handle == nullptr) {
        __android_log_print(ANDROID_LOG_DEBUG, "VrJNI", "handle is NULL, need dlopen() first");
        return nullptr;
    }
    
    VR_EngineIF* pEngineIF = nullptr;
    if ("Baidu_POC" == strEngineType) {
        __android_log_print(ANDROID_LOG_DEBUG, "VrJNI", "Baidu_POC start create");
        fnCreateBaiduPocEngine funcBaiduPoc = (fnCreateBaiduPocEngine)dlsym(handle, VR_CREATE_BAIDU_POC_ENGINE);
        if (!funcBaiduPoc) {
            {
                static bool errorLogMark = false;
                if (!errorLogMark) {
                    errorLogMark = true;
                    printf("dlsym failed, funcBaiduPoc = %s\n", VR_CREATE_BAIDU_POC_ENGINE);
                }
            }

            __android_log_print(ANDROID_LOG_DEBUG, "VrJNI", "dlsym failed Baidu_POC create fail");
            return nullptr;
        }
        if (listener == nullptr) {
            __android_log_print(ANDROID_LOG_DEBUG, "VrJNI", "listener is nullptr");
            return nullptr;
        }

        pEngineIF = (*funcBaiduPoc)(listener);
    }

    if (pEngineIF == nullptr) {
        {
            static bool errorLogMark = false;
            if (!errorLogMark) {
                errorLogMark = true;
            }
        }

        __android_log_print(ANDROID_LOG_DEBUG, "VrJNI", "create engine instance failed");
        return nullptr;
    }

    __android_log_print(ANDROID_LOG_DEBUG, "VrJNI", "####################### create engine %s ok ############################", strEngineType.c_str());
    return pEngineIF;
}

bool VR_EngineFactory::LoadEngineLibrary(const std::string& strEngineType)
{
    //printf("LoadEngineLibrary\n");
     __android_log_print(ANDROID_LOG_DEBUG, "VrJNI", "LoadEngineLibrary ");
    if (strEngineType.empty()) {
        return false;
    }
    map<std::string, std::string>::const_iterator it;
    it = VR_EngineLibraryNameMap.find(strEngineType);
    if (it == VR_EngineLibraryNameMap.cend()) {
        {
            static bool errorLogMark = false;
            if (!errorLogMark) {
                errorLogMark = true;
            }
        }
        return false;
    }

    std::string strLibraryName = it->second;
    void* handle = VR_LoadLibrary(strLibraryName);
    if (nullptr == handle) {
        return false;
    }
    VR_EngineLibraryHandleMap[it->first] = handle;
    return true;
}

VR_EngineIF* VR_EngineFactory::CreateEngineInstance(const std::string& strEngineType, VR_EngineListenerIF* listener)
{
    if (strEngineType.empty()) {
        return nullptr;
    }

    VR_EngineIF* pEngineIF = VR_LoadEngineInstance(strEngineType, listener);
    if (nullptr == pEngineIF) {
        //printf("Create engine %s failed !\n", strEngineType.c_str());
         __android_log_print(ANDROID_LOG_DEBUG, "VrJNI", "Create engine %s failed ! ",strEngineType.c_str());
    }
    return pEngineIF;
}

bool VR_EngineFactory::DestroyEngineInstance(const std::string& strEngineType, VR_EngineIF* engine)
{
    if (strEngineType.empty()) {
        return false;
    }

    map<std::string, void *>::iterator it;
    it = VR_EngineLibraryHandleMap.find(strEngineType);
    if (it == VR_EngineLibraryHandleMap.end()) {
        {
            static bool errorLogMark = false;
            if (!errorLogMark) {
                errorLogMark = true;
                __android_log_print(ANDROID_LOG_DEBUG, "VrJNI", "can not destroy engine: %s", strEngineType.c_str());
            }
        }
        return false;
    }

    void* handle = it->second;
    if (!handle) {
        __android_log_print(ANDROID_LOG_DEBUG, "VrJNI", "handle is NULL, need dlopen() first !");
    }

    if ("Baidu_POC" == strEngineType) {
        fnDestroyBaiduPocEngine func = (fnDestroyBaiduPocEngine)dlsym(handle, VR_DESTROY_BAIDU_POC_ENGINE);
        if (!func) {
            {
                static bool errorLogMark = false;
                if (!errorLogMark) {
                    errorLogMark = true;
                    __android_log_print(ANDROID_LOG_DEBUG, "VrJNI", "dlsym failed, func = %s", VR_CREATE_BAIDU_POC_ENGINE);
                }
            }
            return false;
        }

        (*func)(engine);
    }

    dlclose(it->second);
    VR_EngineLibraryHandleMap.erase(it);

    __android_log_print(ANDROID_LOG_DEBUG, "VrJNI", "destroy engine %s ok", strEngineType.c_str());
    return true;
}

/* EOF */
