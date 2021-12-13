//
// Created by Big-old_luorun$_LQ on 2021/12/12.
//
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

#include "cartoondet.h"
#include<iostream>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <android/log.h>

#include "cpu.h"

CartoonDet::CartoonDet()
{
    blob_pool_allocator.set_size_compare_ratio(0.f);
    workspace_pool_allocator.set_size_compare_ratio(0.f);

    is_first = 1;
}

int CartoonDet::load(const char* modeltype,bool use_gpu)
{
    faceseg.clear();
    blob_pool_allocator.clear();
    workspace_pool_allocator.clear();

    ncnn::set_cpu_powersave(2);
    ncnn::set_omp_num_threads(ncnn::get_big_cpu_count());

    faceseg.opt = ncnn::Option();

#if NCNN_VULKAN
    faceseg.opt.use_vulkan_compute = use_gpu;
#endif

    faceseg.opt.num_threads = ncnn::get_big_cpu_count();
    faceseg.opt.blob_allocator = &blob_pool_allocator;
    faceseg.opt.workspace_allocator = &workspace_pool_allocator;

    char parampath[256];
    char modelpath[256];
    sprintf(parampath, "%s.param", modeltype);
    sprintf(modelpath, "%s.bin", modeltype);

    faceseg.load_param(parampath);
    faceseg.load_model(modelpath);

    return 0;
}

int CartoonDet::load(AAssetManager* mgr, const char* modeltype,  bool use_gpu)
{
    faceseg.clear();
    blob_pool_allocator.clear();
    workspace_pool_allocator.clear();

    ncnn::set_cpu_powersave(2);
    ncnn::set_omp_num_threads(ncnn::get_big_cpu_count());

    faceseg.opt = ncnn::Option();

#if NCNN_VULKAN
    faceseg.opt.use_vulkan_compute = use_gpu;
#endif

    faceseg.opt.num_threads = ncnn::get_big_cpu_count();
    faceseg.opt.blob_allocator = &blob_pool_allocator;
    faceseg.opt.workspace_allocator = &workspace_pool_allocator;
    char parampath[256];
    char modelpath[256];
    sprintf(parampath, "%s.param", modeltype);
    sprintf(modelpath, "%s.bin", modeltype);

    faceseg.load_param(mgr,parampath);
    faceseg.load_model(mgr,modelpath);


    return 0;
}


void CartoonDet::cartoonize(cv::Mat &rgb, cv::Mat &cartoon)
{
    ncnn::Extractor ex_face = faceseg.create_extractor();
    ncnn::Mat ncnn_in;
    w=rgb.cols;
    h=rgb.rows;
    cv::Mat res;
    if((h>w?w:h)>720)
    {
        if (h>w)
        {
            h=(int)(720 * h / w);
            w=720;
        }
        else
        {
            h=720;
            w=(int)(720 * w / h);
        }
    }
    cv::Size ResSize=cv::Size((int)(w/8)*8,(int)(h/8)*8);
    res=cv::Mat(ResSize, rgb.type());
    cv::resize(rgb, res, ResSize,0,0,2);
    ncnn_in = ncnn::Mat::from_pixels_resize(res.data,ncnn::Mat::PIXEL_RGB, res.cols, res.rows,res.cols,res.rows);

    const float means[3] = {127.5,127.5,127.5};
    const float norms[3] = {1/127.5,1/127.5,1/127.5};
    ncnn_in.substract_mean_normalize(means, norms);

    ex_face.input("image", ncnn_in);

    ncnn::Mat cartoon_image;

    ex_face.extract("cartoon_image",cartoon_image);

    float *cartoon_data = (float*)cartoon_image.data;

    cv::Mat cv_cartoon = cv::Mat::zeros(cartoon_image.h, cartoon_image.w, CV_8UC3);
    cv::Mat cv_cartoon_pres = cv::Mat(cartoon_image.h, cartoon_image.w, CV_32FC3);

    for (int i = 0; i < cartoon_image.h; i++)
    {
        for (int j = 0; j < cartoon_image.w; j++)
        {
            cv_cartoon_pres.at<cv::Vec3f>(i, j)[0] = (cartoon_data[0 * cartoon_image.h * cartoon_image.w + i * cartoon_image.w + j]+1)*127.5;
            cv_cartoon_pres.at<cv::Vec3f>(i, j)[1] = (cartoon_data[1 * cartoon_image.h * cartoon_image.w + i * cartoon_image.w + j]+1)*127.5;
            cv_cartoon_pres.at<cv::Vec3f>(i, j)[2] = (cartoon_data[2 * cartoon_image.h * cartoon_image.w + i * cartoon_image.w + j]+1)*127.5;
        }
    }

    cv_cartoon_pres.convertTo(cv_cartoon, CV_8UC3, 1.0, 0);
    cv_cartoon.copyTo(cartoon);
    is_first = 0;
}

int CartoonDet::draw(cv::Mat& rgb)
{
    cv::Mat cartoon;
    cartoonize(rgb, cartoon);
    cv::resize(cartoon, rgb, rgb.size(), 0, 0, 1);
    return 0;
}

