#ifndef J2C_TRIGGER_H
#define J2C_TRIGGER_H

#include "jni.h"
#include <string>

class JNIHelper {
public:
    JNIHelper();
    ~JNIHelper();

    /**
     * Initialize the Java environment and get the static method id
     */
    static bool Initialize(JNIEnv* e, jclass c);

    /**
     * Send the message we retrieve from VR Service to Java UI
     * iMessage means the status in which VR Status locate currently
     */
    static void CallBackVrMsg(const std::string& name);

private:
    static JavaVM*      m_pJvm;
    static jclass       m_pClass;
    static jmethodID    m_pMethod;
    const static std::string METHOD_NAME;
    const static std::string METHOD_TAG;
};

#endif
