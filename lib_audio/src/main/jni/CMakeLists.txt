cmake_minimum_required(VERSION 3.10.2)
# 头文件
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/)
add_library(
        dualrecorder
        SHARED
        dualrecorder.cpp
)
#add_library(alsa SHARED IMPORTED )
#set_target_properties(alsa PROPERTIES
#    IMPORTED_LOCATION "${CMAKE_CURRENT_SOURCE_DIR}/libs/${ANDROID_ABI}/libtinyalsa.so"
# )
find_library(
        alsa-lib
        NAMES asound
        PATHS ${CMAKE_CURRENT_SOURCE_DIR}/libs
)
message("ABI = ${ANDROID_ABI}")
find_library(log-lib log)

target_link_libraries(
     dualrecorder
     ${log-lib}
     alsa
)