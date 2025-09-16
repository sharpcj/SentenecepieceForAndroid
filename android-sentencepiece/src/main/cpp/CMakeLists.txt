cmake_minimum_required(VERSION 3.22)

project(spm_android LANGUAGES C CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

if(ANDROID)
	add_definitions(-DANDROID -DNDEBUG)
endif()

# SentencePiece
set(SPM_ROOT ${CMAKE_CURRENT_SOURCE_DIR}/third_party/sentencepiece)

# Configure upstream options
set(SPM_ENABLE_SHARED OFF CACHE BOOL "" FORCE)
set(SPM_BUILD_TEST OFF CACHE BOOL "" FORCE)
set(SPM_ENABLE_TCMALLOC OFF CACHE BOOL "" FORCE)
set(SPM_NO_THREADLOCAL OFF CACHE BOOL "" FORCE)
set(SPM_DISABLE_EMBEDDED_DATA OFF CACHE BOOL "" FORCE)
set(SPM_PROTOBUF_PROVIDER "internal" CACHE STRING "" FORCE)
set(SPM_ABSL_PROVIDER "internal" CACHE STRING "" FORCE)

add_subdirectory(${SPM_ROOT} ${CMAKE_CURRENT_BINARY_DIR}/spm_build)

# Exclude upstream CLI tools from default build
foreach(bin_target spm_encode spm_decode spm_normalize spm_train spm_export_vocab)
	if(TARGET ${bin_target})
		set_target_properties(${bin_target} PROPERTIES EXCLUDE_FROM_ALL TRUE EXCLUDE_FROM_DEFAULT_BUILD TRUE)
	endif()
endforeach()

add_library(spm_android SHARED
	spm_jni.cpp
)

target_include_directories(spm_android PRIVATE ${SPM_ROOT}/src ${SPM_ROOT})

target_link_libraries(spm_android
	android
	log
	sentencepiece-static
)