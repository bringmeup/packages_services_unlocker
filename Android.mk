LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

ifeq ($(TARGET_BUILD_APPS),)
support_library_root_dir := frameworks/support
else
support_library_root_dir := prebuilts/sdk/current/support
endif

LOCAL_MODULE_TAGS := optional
# Reference java/ and aidl/ source files
LOCAL_SRC_FILES := \
    $(call all-java-files-under, app/src/main/java) \
    $(call all-named-files-under, *.aidl, app/src/main/aidl)

# no resources -- commented out
#LOCAL_ASSET_DIR := $(LOCAL_PATH)/app/src/main/assets
LOCAL_MANIFEST_FILE := app/src/main/AndroidManifest.xml

# Re-map res/ and assets/ directly
LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/app/src/main/res \
    $(support_library_root_dir)/v7/cardview/res \
    $(support_library_root_dir)/v7/recyclerview/res \
    $(support_library_root_dir)/v7/appcompat/res \
    $(support_library_root_dir)/design/res
    
LOCAL_AAPT_FLAGS := \
    --auto-add-overlay \
    --extra-packages android.support.v7.appcompat \
    --extra-packages android.support.v7.cardview \
    --extra-packages android.support.v7.recyclerview \
    --extra-packages android.support.design 


LOCAL_STATIC_JAVA_LIBRARIES := \
    android-common \
    android-support-v13 \
    android-support-v4 \
    android-support-v7-appcompat \
    android-support-v7-cardview \
    android-support-v7-recyclerview \
    android-support-design

LOCAL_PACKAGE_NAME := Unlocker
LOCAL_SDK_VERSION := current
LOCAL_PROGUARD_ENABLED := disabled
LOCAL_CERTIFICATE := platform

include $(BUILD_PACKAGE)

#######################################

# no native part -- everything commented-out

#include $(CLEAR_VARS)
## Re-map native code path
#LOCAL_SRC_FILES:= \
#    $(call all-cpp-files-under, app/src/main/jni)
#
#LOCAL_MODULE := mypackage_jni
#
#include $(BUILD_SHARED_LIBRARY)


