#pragma once

#include <jni.h>

namespace ratio::java
{

  class scoped_env
  {
  public:
    scoped_env(JNIEnv *env);

    JNIEnv *get_env();

  private:
    class detach_on_exit
    {
    public:
      detach_on_exit(JavaVM *jvm) : jvm(jvm) {}
      ~detach_on_exit() { jvm->DetachCurrentThread(); }

    private:
      JavaVM *jvm;
    };

    JavaVM *jvm;
    JNIEnv *env;
  };
} // namespace ratio::java