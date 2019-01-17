#---------------------------------------------
#----------global parameter ----------  ------
#---------------------------------------------
LIBBASEROOT := ivi/frameworks/service/navi/libbase
NCBASEROOT := ivi

BASE_GLOBAL_INC := \
    external/protobuf/src/ \
    $(PRODUCT_OUT)/obj/include \
    $(LIBBASEROOT)/base/memorytracker/include \
    $(LIBBASEROOT)/base/commonlib/aplcommonlib \
    $(LIBBASEROOT)/base/commonlib/magiclog/include \
    $(LIBBASEROOT)/base/commonlib/aplcommonlib/include \
	$(LIBBASEROOT)/base/eventsys/include \
	$(LIBBASEROOT)/base/eventsys/NaviEventFactory/include \
	$(LIBBASEROOT)/base/protofiles \
    $(LIBBASEROOT)/externals/boost \
    $(NCBASEROOT)/system/core/include \
    $(NCBASEROOT)/platform/service/connectmanager/usb/handler/libnceventsys \

VOICECOMMROOT := ivi/frameworks/service/navi/voice/vrservice/voicecomm

#---------------------------------------------
#------project   libvoicerecog-navi    -------
#---------------------------------------------
# LOCAL_PATH := $(call my-dir)
# include $(CLEAR_VARS)

# LOCAL_CFLAGS = -fvisibility=hidden -Wall \

# LOCAL_CPPFLAGS = -fexceptions -include MEM_new.h -fvisibility=hidden -Wall -Wno-unused-parameter \

# #module special flags
# LOCAL_SHARED_LIBRARIES += \
# 	libmemtracker-navi \
# 	libvoicecomm-navi \
# 	libeventsyscore-navi \
# 	libeventfactory-navi \
# 	libnaviprotosrc \
# 	libprotobuf-cpp-full \
# 	libaplcommon-navi  \

# LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog

# LOCAL_C_INCLUDES += \
# 		$(LOCAL_PATH) \
#         $(BASE_GLOBAL_INC) \

# LOCAL_SRC_FILES := \
# 		JNIHelper.cpp \
# 		net_suntec_merbok_jni_vr_UIVrControlJNI.cpp \
# 		net_suntec_merbok_jni_vr_UIVrMessageJNI.cpp \
# 		VoiceRecogProxy.cpp

# LOCAL_MODULE = libvrproxy-navi

# LOCAL_ADDITIONAL_DEPENDENCIES := libnaviprotosrc

# include $(BUILD_SHARED_LIBRARY)


#---------------------------------------------
#------xj   -------
#---------------------------------------------
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE = libDialogEnginexj

LOCAL_CFLAGS = -fvisibility=hidden -Wall \

LOCAL_C_INCLUDES := \
    VR_DialogEngine.h \
    vr_suntec_net_vrapp_vrService_vrEngine_BaiduPocJNI.h \
	vr_suntec_net_vrapp_vrService_vrEngine_VrEngineJNICreate.h \
    VR_EngineFactory.h \
    VR_EngineIF.h \
    VR_EngineListener.h \
    VR_EngineListenerIF.h \
	JNIHelper.cpp \

LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog

LOCAL_SRC_FILES := \
    VR_DialogEngine.cpp \
    VR_EngineFactory.cpp \
    VR_EngineListener.cpp \
    vr_suntec_net_vrapp_vrService_vrEngine_BaiduPocJNI.cpp \
	vr_suntec_net_vrapp_vrService_vrEngine_VrEngineJNICreate.cpp \
	JNIHelper.cpp \

include $(BUILD_SHARED_LIBRARY)
