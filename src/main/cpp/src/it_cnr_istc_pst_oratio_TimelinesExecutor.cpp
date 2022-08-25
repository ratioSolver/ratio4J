#include "it_cnr_istc_pst_oratio_TimelinesExecutor.h"
#include "java_executor_listener.h"

inline ratio::solver::solver *get_solver_from_executor(JNIEnv *env, jobject obj)
{
    jobject slv_obj = env->GetObjectField(obj, env->GetFieldID(env->GetObjectClass(obj), "solver", "Lit/cnr/istc/pst/oratio/Solver;"));
    return reinterpret_cast<ratio::solver::solver *>(env->GetLongField(slv_obj, env->GetFieldID(env->GetObjectClass(slv_obj), "native_handle", "J")));
}

inline ratio::executor::executor *get_executor(JNIEnv *env, jobject obj) { return reinterpret_cast<ratio::executor::executor *>(env->GetLongField(obj, env->GetFieldID(env->GetObjectClass(obj), "native_handle", "J"))); }
inline ratio::java::java_executor_listener *get_executor_listener(JNIEnv *env, jobject obj) { return reinterpret_cast<ratio::java::java_executor_listener *>(env->GetLongField(obj, env->GetFieldID(env->GetObjectClass(obj), "executor_listener_native_handle", "J"))); }

JNIEXPORT jlong JNICALL Java_it_cnr_istc_pst_oratio_TimelinesExecutor_new_1instance(JNIEnv *env, jobject obj, jlong units_per_tick_num, jlong units_per_tick_den)
{
    auto *s = get_solver_from_executor(env, obj);
    auto *exec = new ratio::executor::executor(*s, semitone::rational(static_cast<semitone::I>(units_per_tick_num), static_cast<semitone::I>(units_per_tick_den)));

    auto *el = new ratio::java::java_executor_listener(*exec, env, obj);
    env->SetLongField(obj, env->GetFieldID(env->GetObjectClass(obj), "executor_listener_native_handle", "J"), reinterpret_cast<jlong>(el));

    return reinterpret_cast<jlong>(exec);
}

JNIEXPORT void JNICALL Java_it_cnr_istc_pst_oratio_TimelinesExecutor_dispose(JNIEnv *env, jobject obj)
{
    delete get_executor_listener(env, obj);
    env->SetLongField(obj, env->GetFieldID(env->GetObjectClass(obj), "executor_listener_native_handle", "J"), 0);
    delete get_executor(env, obj);
    env->SetLongField(obj, env->GetFieldID(env->GetObjectClass(obj), "native_handle", "J"), 0);
}

JNIEXPORT void JNICALL Java_it_cnr_istc_pst_oratio_TimelinesExecutor_tick(JNIEnv *env, jobject obj)
{
    try
    {
        get_executor(env, obj)->tick();
    }
    catch (const std::exception &e)
    {
        env->ThrowNew(env->FindClass("it/cnr/istc/pst/oratio/ExecutorException"), e.what());
    }
}

JNIEXPORT void JNICALL Java_it_cnr_istc_pst_oratio_TimelinesExecutor_dont_1start_1yet(JNIEnv *env, jobject obj, jlongArray atoms, jlongArray nums, jlongArray dens)
{
    const jsize atms_size = env->GetArrayLength(atoms);
    std::vector<jlong> j_atms(atms_size);
    env->GetLongArrayRegion(atoms, 0, atms_size, j_atms.data());
    const jsize nums_size = env->GetArrayLength(nums);
    std::vector<jlong> j_nums(nums_size);
    env->GetLongArrayRegion(nums, 0, nums_size, j_nums.data());
    const jsize dens_size = env->GetArrayLength(dens);
    std::vector<jlong> j_dens(dens_size);
    env->GetLongArrayRegion(dens, 0, dens_size, j_dens.data());

    auto &exec = *get_executor(env, obj);
    std::unordered_map<const ratio::core::atom *, semitone::rational> atms;
    for (jsize i = 0; i < atms_size; i++)
        atms[reinterpret_cast<ratio::core::atom *>(j_atms[i])] = semitone::rational(j_nums[i], j_dens[i]);

    exec.dont_start_yet(atms);
}

JNIEXPORT void JNICALL Java_it_cnr_istc_pst_oratio_TimelinesExecutor_dont_1end_1yet(JNIEnv *env, jobject obj, jlongArray atoms, jlongArray nums, jlongArray dens)
{
    const jsize atms_size = env->GetArrayLength(atoms);
    std::vector<jlong> j_atms(atms_size);
    env->GetLongArrayRegion(atoms, 0, atms_size, j_atms.data());
    const jsize nums_size = env->GetArrayLength(nums);
    std::vector<jlong> j_nums(nums_size);
    env->GetLongArrayRegion(nums, 0, nums_size, j_nums.data());
    const jsize dens_size = env->GetArrayLength(dens);
    std::vector<jlong> j_dens(dens_size);
    env->GetLongArrayRegion(dens, 0, dens_size, j_dens.data());

    auto &exec = *get_executor(env, obj);
    std::unordered_map<const ratio::core::atom *, semitone::rational> atms;
    for (jsize i = 0; i < atms_size; i++)
        atms[reinterpret_cast<ratio::core::atom *>(j_atms[i])] = semitone::rational(j_nums[i], j_dens[i]);

    exec.dont_start_yet(atms);
}

JNIEXPORT void JNICALL Java_it_cnr_istc_pst_oratio_TimelinesExecutor_failure(JNIEnv *env, jobject obj, jlongArray atoms)
{
    const jsize atms_size = env->GetArrayLength(atoms);
    std::vector<jlong> j_atms(atms_size);
    env->GetLongArrayRegion(atoms, 0, atms_size, j_atms.data());

    auto &exec = *get_executor(env, obj);
    std::unordered_set<const ratio::core::atom *> atms;
    for (jsize i = 0; i < atms_size; i++)
        atms.insert(reinterpret_cast<ratio::core::atom *>(j_atms[i]));

    try
    {
        exec.failure(atms);
    }
    catch (const std::exception &e)
    {
        env->ThrowNew(env->FindClass("it/cnr/istc/pst/oratio/ExecutorException"), e.what());
    }
}