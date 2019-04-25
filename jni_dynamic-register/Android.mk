LOCAL_PATH :=$(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE        :=  libnmvr_jni

LOCAL_MODULE_TAGS   := optional

LOCAL_C_INCLUDES := $(shell find $(LOCAL_PATH) -type d)
LOCAL_C_INCLUDES += \
        $(LOCAL_PATH) \
        $(LOCAL_PATH)/include \
        $(LOCAL_PATH)/src/include \
        frameworks/base/core \
        frameworks/native/include \
        frameworks/native/libs/binder/include \
        system/core/include \
        ivi/system/core/ \
        ivi/system/core/include \
        ivi/frameworks/service/vr/native/voicerecog/voicerecoglib \
        ivi/frameworks/service/vr/native/proxy/include \
        ivi/frameworks/service/vr/native/externals/pugixml \
        ivi/frameworks/service/vr/native/externals/rapidjson/include \
        ivi/frameworks/service/vr/native/vrtask \


LOCAL_SRC_FILES := src/src/com_iauto_baiduvoice_VrBaiduService_jni.cpp
LOCAL_SRC_FILES += src/src/VrBaiduJniListener.cpp

LOCAL_CFLAGS += \
        -Wno-format \
        -std=c++11 \
        -fexceptions \
        -Wno-unused-parameter \
        -Wno-undefined-bool-conversion \

LOCAL_VENDOR_MODULE := true

LOCAL_STATIC_LIBRARIES := libtinyxml

LOCAL_SHARED_LIBRARIES := \
        libutils \
        libbinder \
        libncore \
        liblog \
        libandroid_runtime \
        libnativehelper \
        libvoicerecog-navi \
        libvraidlservice \

LOCAL_PROGUARD_ENABLED:= disabled

include $(BUILD_SHARED_LIBRARY)