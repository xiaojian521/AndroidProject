/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class net_suntec_merbok_jni_vr_UIVrControlJNI */

#ifndef _Included_net_suntec_merbok_jni_vr_UIVrControlJNI
#define _Included_net_suntec_merbok_jni_vr_UIVrControlJNI
#ifdef __cplusplus
extern "C" {
#endif
#undef net_suntec_merbok_jni_vr_UIVrControlJNI_VR_STATUS_IDLE
#define net_suntec_merbok_jni_vr_UIVrControlJNI_VR_STATUS_IDLE 0L
#undef net_suntec_merbok_jni_vr_UIVrControlJNI_VR_STATUS_LISTENING
#define net_suntec_merbok_jni_vr_UIVrControlJNI_VR_STATUS_LISTENING 1L
#undef net_suntec_merbok_jni_vr_UIVrControlJNI_VR_STATUS_PROCESSING
#define net_suntec_merbok_jni_vr_UIVrControlJNI_VR_STATUS_PROCESSING 2L
#undef net_suntec_merbok_jni_vr_UIVrControlJNI_VR_STATUS_PLAYING
#define net_suntec_merbok_jni_vr_UIVrControlJNI_VR_STATUS_PLAYING 3L
#undef net_suntec_merbok_jni_vr_UIVrControlJNI_VR_SCREEN_OPEN
#define net_suntec_merbok_jni_vr_UIVrControlJNI_VR_SCREEN_OPEN 0L
#undef net_suntec_merbok_jni_vr_UIVrControlJNI_VR_SCREEN_CLOSE
#define net_suntec_merbok_jni_vr_UIVrControlJNI_VR_SCREEN_CLOSE 1L
/*
 * Class:     net_suntec_merbok_jni_vr_UIVrControlJNI
 * Method:    GetCurrentVrStatus
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_net_suntec_merbok_jni_vr_UIVrControlJNI_GetCurrentVrStatus
  (JNIEnv *, jclass);

/*
 * Class:     net_suntec_merbok_jni_vr_UIVrControlJNI
 * Method:    StartVR
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_net_suntec_merbok_jni_vr_UIVrControlJNI_StartVR
  (JNIEnv *, jclass);

/*
 * Class:     net_suntec_merbok_jni_vr_UIVrControlJNI
 * Method:    ExitVR
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_net_suntec_merbok_jni_vr_UIVrControlJNI_ExitVR
  (JNIEnv *, jclass);

/*
 * Class:     net_suntec_merbok_jni_vr_UIVrControlJNI
 * Method:    GetCurrentVolume
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_net_suntec_merbok_jni_vr_UIVrControlJNI_GetCurrentVolume
  (JNIEnv *, jclass);

/*
 * Class:     net_suntec_merbok_jni_vr_UIVrControlJNI
 * Method:    getContentResult
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_net_suntec_merbok_jni_vr_UIVrControlJNI_getContentResult
  (JNIEnv *, jclass);

/*
 * Class:     net_suntec_merbok_jni_vr_UIVrControlJNI
 * Method:    ResetVr
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_net_suntec_merbok_jni_vr_UIVrControlJNI_ResetVr
  (JNIEnv *, jclass);

/*
 * Class:     net_suntec_merbok_jni_vr_UIVrControlJNI
 * Method:    SetUseVr
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_net_suntec_merbok_jni_vr_UIVrControlJNI_SetUseVr
  (JNIEnv *, jclass, jboolean);

/*
 * Class:     net_suntec_merbok_jni_vr_UIVrControlJNI
 * Method:    SetEngineUseVr
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_net_suntec_merbok_jni_vr_UIVrControlJNI_SetEngineUseVr
  (JNIEnv *, jclass, jstring);

#ifdef __cplusplus
}
#endif
#endif