// Tencent is pleased to support the open source community by making ncnn available.
//
// Copyright (C) 2021 THL A29 Limited, a Tencent company. All rights reserved.
//
// Licensed under the BSD 3-Clause License (the "License"); you may not use this file except
// in compliance with the License. You may obtain a copy of the License at
//
// https://opensource.org/licenses/BSD-3-Clause
//
// Unless required by applicable law or agreed to in writing, software distributed
// under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
// CONDITIONS OF ANY KIND, either express or implied. See the License for the
// specific language governing permissions and limitations under the License.

#include <android/asset_manager_jni.h>
#include <android/native_window_jni.h>
#include <android/native_window.h>

#include <android/log.h>

#include <jni.h>

#include <string>
#include <vector>

#include <platform.h>
#include <benchmark.h>

#include "nanodet.h"


#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>

#if __ARM_NEON
#include <arm_neon.h>
#endif // __ARM_NEON


static NanoDet* g_nanodet = 0;
static ncnn::Mutex lock;


extern "C" {

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "JNI_OnLoad");


    return JNI_VERSION_1_4;
}

JNIEXPORT void JNI_OnUnload(JavaVM *vm, void *reserved) {
    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "JNI_OnUnload");

    {
        ncnn::MutexLockGuard g(lock);

        delete g_nanodet;
        g_nanodet = 0;
    }

}

// public native boolean loadModel(AssetManager mgr, int modelid, int cpugpu);
JNIEXPORT jboolean JNICALL
Java_com_example_image_NcnnBodyseg_loadModel(JNIEnv *env, jobject thiz, jobject assetManager,
                                             jint modelid, jint cpugpu) {
    if (modelid < 0 || modelid > 6 || cpugpu < 0 || cpugpu > 1) {
        return JNI_FALSE;
    }

    AAssetManager *mgr = AAssetManager_fromJava(env, assetManager);

    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "loadModel %p", mgr);

    const char *modeltypes[] =
            {
                    "rvm-512",
                    "rvm-640",
            };

    const int target_sizes[] =
            {
                    512,
                    640,
            };

    const float mean_vals[][3] =
            {
                    {123.675f, 116.28f, 103.53f},
                    {123.675f, 116.28f, 103.53f},
            };

    const float norm_vals[][3] =
            {
                    {0.01712475f, 0.0175f, 0.01742919f},
                    {0.01712475f, 0.0175f, 0.01742919f},
            };

    const char *modeltype = modeltypes[(int) modelid];
    int target_size = target_sizes[(int) modelid];
    bool use_gpu = (int) cpugpu == 1;

    // reload
    {
        ncnn::MutexLockGuard g(lock);

        if (use_gpu && ncnn::get_gpu_count() == 0) {
            // no gpu
            delete g_nanodet;
            g_nanodet = 0;
        } else {
            if (!g_nanodet)
                g_nanodet = new NanoDet;
            g_nanodet->load(mgr, modeltype, target_size, mean_vals[(int) modelid],
                            norm_vals[(int) modelid], use_gpu);
        }
    }

    return JNI_TRUE;
}

JNIEXPORT jobject JNICALL
Java_com_example_image_NcnnBodyseg_matting(JNIEnv *env, jobject thiz, jobject bitmap)
{
    {
        ncnn::MutexLockGuard g(lock);
        if (g_nanodet) {
            int *data = NULL;
            AndroidBitmapInfo info = {0};
            AndroidBitmap_getInfo(env, bitmap, &info);
            AndroidBitmap_lockPixels(env, bitmap, (void **) &data);
            // 检查图片格式
            if (info.format == ANDROID_BITMAP_FORMAT_RGBA_8888) {
                __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "info format is RGBA");
            } else if (info.format == ANDROID_BITMAP_FORMAT_RGB_565) {
                __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "info format is RGB");
            } else {
                __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "Unknown Format");
            }
            cv::Mat test(info.height, info.width, CV_8UC4, (char *) data); // RGBA
            cv::Mat img_bgr;
            cv::cvtColor(test, img_bgr, cv::COLOR_RGBA2RGB);
            g_nanodet->draw(img_bgr);
            jclass java_bitmap_class = (jclass) env->FindClass("android/graphics/Bitmap");
            jmethodID mid = env->GetMethodID(java_bitmap_class, "getConfig",
                                             "()Landroid/graphics/Bitmap$Config;");
            jobject bitmap_config = env->CallObjectMethod(bitmap, mid);
            bool needPremultiplyAlpha = false;
            cv::Mat &src = img_bgr;
            {
                jclass java_bitmap_class = (jclass) env->FindClass("android/graphics/Bitmap");
                jmethodID mid = env->GetStaticMethodID(java_bitmap_class,
                                                       "createBitmap",
                                                       "(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");

                jobject bitmap = env->CallStaticObjectMethod(java_bitmap_class,
                                                             mid, src.size().width,
                                                             src.size().height, bitmap_config);
                AndroidBitmapInfo info;
                void *pixels = 0;

                {
                    CV_Assert(AndroidBitmap_getInfo(env, bitmap, &info) >= 0);
                    CV_Assert(src.type() == CV_8UC1 || src.type() == CV_8UC3 ||
                              src.type() == CV_8UC4);
                    CV_Assert(AndroidBitmap_lockPixels(env, bitmap, &pixels) >= 0);
                    CV_Assert(pixels);
                    if (info.format == ANDROID_BITMAP_FORMAT_RGBA_8888) {
                        cv::Mat tmp(info.height, info.width, CV_8UC4, pixels);
                        if (src.type() == CV_8UC1) {
                            cvtColor(src, tmp, cv::COLOR_GRAY2RGBA);
                        } else if (src.type() == CV_8UC3) {
                            cvtColor(src, tmp, cv::COLOR_RGB2BGRA);
                            cvtColor(tmp, tmp, cv::COLOR_BGRA2RGBA);
                        } else if (src.type() == CV_8UC4) {
                            if (needPremultiplyAlpha) {
                                cvtColor(src, tmp, cv::COLOR_RGBA2mRGBA);
                            } else {
                                src.copyTo(tmp);
                            }
                        }
                    } else {
                        // info.format == ANDROID_BITMAP_FORMAT_RGB_565
                        cv::Mat tmp(info.height, info.width, CV_8UC2, pixels);
                        if (src.type() == CV_8UC1) {
                            cvtColor(src, tmp, cv::COLOR_GRAY2BGR565);
                        } else if (src.type() == CV_8UC3) {
                            cvtColor(src, tmp, cv::COLOR_RGB2BGR565);
                        } else if (src.type() == CV_8UC4) {
                            cvtColor(src, tmp, cv::COLOR_RGBA2BGR565);
                        }
                    }
                    AndroidBitmap_unlockPixels(env, bitmap);
                    return bitmap;
                }
            }
        } else {
            __android_log_print(ANDROID_LOG_ERROR, "ncnn",
                                "g_nanodet no initialise please load model first");
            return nullptr;
        }
    }
}
}