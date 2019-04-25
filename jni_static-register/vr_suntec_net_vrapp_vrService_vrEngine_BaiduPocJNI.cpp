#include "vr_suntec_net_vrapp_vrService_vrEngine_BaiduPocJNI.h"
#include "VR_DialogEngine.h"
#include <android/log.h>
#include <memory>

/*
 * Class:     vr_suntec_net_vrapp_vrService_vrEngine_BaiduPocJNI
 * Method:    SendMessage
 * Signature: (JLjava/lang/String;)V
 */

VR_DialogEngine* m_pDialogEngine =  VR_DialogEngine::getInstance();

JNIEXPORT void JNICALL Java_vr_suntec_net_vrapp_vrService_vrEngine_BaiduPocJNI_SendMessage
  (JNIEnv * env, jclass obj, jstring msg)
  {
    __android_log_print(ANDROID_LOG_DEBUG, "VrJNI", "SendMessage");
    const char* str;
    str = env->GetStringUTFChars(msg, NULL);
    if(str == NULL) {
         printf("error : SendMessage str is null");
    }
    if (m_pDialogEngine != nullptr) {
      m_pDialogEngine->SendMessage(str);
    }
  }

/*
 * Class:     vr_suntec_net_vrapp_vrService_vrEngine_BaiduPocJNI
 * Method:    StopDiag
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_vr_suntec_net_vrapp_vrService_vrEngine_BaiduPocJNI_StopDiag
  (JNIEnv *env , jclass obj)
  {
    __android_log_print(ANDROID_LOG_DEBUG, "VrJNI", "StopDiag");
    if (m_pDialogEngine != nullptr) {
      m_pDialogEngine->StopDiag();
    }
  }

/*
 * Class:     vr_suntec_net_vrapp_vrService_vrEngine_BaiduPocJNI
 * Method:    StartDiag
 * Signature: (JLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_vr_suntec_net_vrapp_vrService_vrEngine_BaiduPocJNI_StartDiag
  (JNIEnv * env, jclass obj, jstring doMainType)
  {
    __android_log_print(ANDROID_LOG_DEBUG, "VrJNI", "StartDiag");
    const char* str;
    str = env->GetStringUTFChars(doMainType, NULL);
    if(str == NULL) {
         printf("error : StartDiag str is null");
    }

    if (m_pDialogEngine != nullptr) {
      m_pDialogEngine->StartDiag();
    }
  }

/*
 * Class:     vr_suntec_net_vrapp_vrService_vrEngine_BaiduPocJNI
 * Method:    WriteAudioDataToEngine
 * Signature: (JSI)V
 */
JNIEXPORT void JNICALL Java_vr_suntec_net_vrapp_vrService_vrEngine_BaiduPocJNI_WriteAudioDataToEngine
  (JNIEnv * env, jclass obj, jshortArray pData, jint size)
  {
    __android_log_print(ANDROID_LOG_DEBUG, "VrJNI", "WriteAudioDataToEngine");
    jshort* m_pData =env->GetShortArrayElements(pData,0);
    
    if (m_pDialogEngine != nullptr) {
      m_pDialogEngine->WriteAudioDataToEngine(m_pData, size);
    }
  }
