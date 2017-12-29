#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring

JNICALL
Java_com_hypeagle_androidlearning_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "手持两把锟斤拷，口中疾呼烫烫烫；\n脚踏千朵屯屯屯，笑看万物锘锘锘。";
    return env->NewStringUTF(hello.c_str());
}
