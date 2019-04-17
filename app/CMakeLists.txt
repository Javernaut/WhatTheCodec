cmake_minimum_required(VERSION 3.4.1)

set(ffmpeg_dir ${CMAKE_SOURCE_DIR}/../ffmpeg-android-maker/output)
include_directories(${ffmpeg_dir}/include)

set(ffmpeg_libs ${ffmpeg_dir}/lib/${ANDROID_ABI})

add_library(avutil SHARED IMPORTED)
set_target_properties(avutil PROPERTIES IMPORTED_LOCATION ${ffmpeg_libs}/libavutil.so)

add_library(avformat SHARED IMPORTED)
set_target_properties(avformat PROPERTIES IMPORTED_LOCATION ${ffmpeg_libs}/libavformat.so)

add_library(avcodec SHARED IMPORTED)
set_target_properties(avcodec PROPERTIES IMPORTED_LOCATION ${ffmpeg_libs}/libavcodec.so)

add_library(swscale SHARED IMPORTED)
set_target_properties(swscale PROPERTIES IMPORTED_LOCATION ${ffmpeg_libs}/libswscale.so)

find_library(log-lib log)
find_library(jnigraphics-lib jnigraphics)

add_library(video-config
        SHARED
        src/main/cpp/main.cpp
        src/main/cpp/video_config.cpp
        src/main/cpp/video_config_jni.cpp
        src/main/cpp/utils.cpp)

target_link_libraries(
        video-config
        ${log-lib}
        ${jnigraphics-lib}
        avformat
        avcodec
        swscale
        avutil)