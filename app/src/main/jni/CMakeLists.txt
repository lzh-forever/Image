project(ncnnbodyseg)

cmake_minimum_required(VERSION 3.10)

set(OpenCV_DIR ${CMAKE_SOURCE_DIR}/opencv-mobile-4.5.4-android/sdk/native/jni)
find_package(OpenCV REQUIRED core imgproc)

set(ncnn_DIR ${CMAKE_SOURCE_DIR}/ncnn-20211122-android-vulkan/${ANDROID_ABI}/lib/cmake/ncnn)
find_package(ncnn REQUIRED)

add_library(ncnnbodyseg SHARED nanodetncnn.cpp nanodet.cpp )
add_library(ncnncartoon SHARED cartoonncnn.cpp cartoondet.cpp )

target_link_libraries(ncnnbodyseg ncnn ${OpenCV_LIBS} camera2ndk mediandk)
target_link_libraries(ncnncartoon ncnn ${OpenCV_LIBS} camera2ndk mediandk)
