#
# Copyright (C) 2008 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_STATIC_JAVA_LIBRARIES := supporta supports

LOCAL_STATIC_JAVA_AAR_LIBRARIES := support4

LOCAL_SRC_FILES := $(call all-subdir-Java-files) $(call all-java-files-under, src) $(call all-renderscript-files-under, src)

LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/res


LOCAL_PACKAGE_NAME := OtoLauncher

LOCAL_CERTIFICATE := platform

include $(BUILD_PACKAGE)

include $(CLEAR_VARS)

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := supporta:lib/support-annotations-22.2.1-sources.jar \
                                        supports:lib/internal_impl-22.2.1.jar \
                                        support4:lib/support-v4-22.2.1.aar \
include $(CLEAR_VARS)

LOCAL_AAPT_FLAG := \
                   --auto-add-overlay \
                   --extra-package android.support.v4 \
                   --extra-package com.openthos.launcher.openthoslauncher

LOCAL_MANIFEST_FILE := $(LOCAL_PATH)/AndroidManifest.xml

include $(BUILD_MULTI_PREBUILT)

include $(call all-makefiles-under,$(LOCAL_PATH))
