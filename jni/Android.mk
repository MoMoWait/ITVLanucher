LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := itvbox_shared

LOCAL_MODULE_FILENAME := libitvbox

LOCAL_SRC_FILES := itvbox/main.cpp \
                   ../../Classes/AppDelegate.cpp \
                   ../../Classes/AppScene.cpp \
                   ../../Classes/LayerFactory.cpp \
                   ../../Classes/ResourceManager.cpp \
                   ../../Classes/bridge/Cocos2dxBridge.cpp \
                   ../../Classes/layer/BaseLayer.cpp \
                   ../../Classes/layer/AppLayer.cpp \
                   ../../Classes/layer/CategoryLayer.cpp \
                   ../../Classes/layer/GameLayer.cpp \
                   ../../Classes/layer/HotLayer.cpp \
                   ../../Classes/layer/NetTvLayer.cpp \
                   ../../Classes/layer/SettingLayer.cpp \
                   ../../Classes/model/CellItem.cpp \
                   ../../Classes/model/PackageInformation.cpp \
                   ../../Classes/utils/FileUtils.cpp \
                   ../../Classes/utils/md5.cpp


LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/../../cocos2dx/platform/android/jni

LOCAL_C_INCLUDES := $(LOCAL_PATH)/../../Classes \
					$(LOCAL_PATH)/../../Classes/bridge \
					$(LOCAL_PATH)/../../Classes/layer \
					$(LOCAL_PATH)/../../Classes/model \
					$(LOCAL_PATH)/../../Classes/utils \
					$(LOCAL_PATH)/../../../../cocos2dx/platform/android/jni \
					$(LOCAL_PATH)/../../../../external/jsoncpp/include/json \
					$(LOCAL_PATH)/../../../../external/cocoswidget/include \
					$(JNI_H_INCLUDE)
					

LOCAL_WHOLE_STATIC_LIBRARIES += cocoswidget_static
LOCAL_WHOLE_STATIC_LIBRARIES += jsoncpp_static
LOCAL_WHOLE_STATIC_LIBRARIES += cocos2dx_static
LOCAL_WHOLE_STATIC_LIBRARIES += cocosdenshion_static
LOCAL_WHOLE_STATIC_LIBRARIES += box2d_static
LOCAL_WHOLE_STATIC_LIBRARIES += chipmunk_static
LOCAL_WHOLE_STATIC_LIBRARIES += cocos_extension_static

include $(BUILD_SHARED_LIBRARY)

$(call import-module,external/jsoncpp)
$(call import-module,cocos2dx)
$(call import-module,cocos2dx/platform/third_party/android/prebuilt/libcurl)
$(call import-module,CocosDenshion/android)
$(call import-module,extensions)
$(call import-module,external/Box2D)
$(call import-module,external/chipmunk)
$(call import-module,external/cocoswidget)
