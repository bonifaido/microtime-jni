#include <microtime_MicroTime.h>
#include <sys/time.h>

//#define likely(x)       x
#define likely(x)       __builtin_expect((x),1)

/*
 * Class:     microtime_MicroTime
 * Method:    currentTimeMicros
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_microtime_MicroTime_currentTimeMicros(JNIEnv *env, jclass klass)
{
    struct timeval time;

    if (likely(gettimeofday(&time, NULL) == 0))
    {
        return time.tv_sec * 1000 * 1000 + time.tv_usec;
    }
    else
    {
        return -1;
        // return (*env)->ThrowNew(env, exClass, message);
    }
}
