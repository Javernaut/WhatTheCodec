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

find_library(log-lib log)

add_library(native-lib
        SHARED
        src/main/cpp/native-lib.cpp
        src/main/cpp/video_config.cpp)

target_link_libraries(
        native-lib
        ${log-lib}
        avformat
        avcodec
        avutil)