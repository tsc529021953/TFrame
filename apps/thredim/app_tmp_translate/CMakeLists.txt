cmake_minimum_required(VERSION 3.10)

#配置加载头文件
include_directories(${PROJECT_SOURCE_DIR}/alley/include)

add_library(
        MusicEncode
        SHARED
        src/main/jni/MusicEncode.cpp
)

#动态方式加载
add_library(openssl SHARED IMPORTED )
add_library(ssl SHARED IMPORTED )
#引入第三方.so库
set_target_properties(openssl PROPERTIES IMPORTED_LOCATION ${PROJECT_SOURCE_DIR}/alley/lib/${ANDROID_ABI}/libcrypto.so)
set_target_properties(ssl PROPERTIES IMPORTED_LOCATION ${PROJECT_SOURCE_DIR}/alley/lib/${ANDROID_ABI}/libssl.so)

target_include_directories(
        MusicEncode
        PRIVATE
        ${ANDROID_NDK}/sources/android/native_app_glue
        ${ANDROID_NDK}/sources/android/cpufeatures
)

target_link_libraries(
        MusicEncode
        openssl
        ssl
        android
        log
)
