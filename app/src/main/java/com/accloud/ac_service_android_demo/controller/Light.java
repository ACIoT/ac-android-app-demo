package com.accloud.ac_service_android_demo.controller;

import android.content.Context;
import android.widget.Toast;

import com.accloud.ac_service_android_demo.R;
import com.accloud.ac_service_android_demo.config.Config;
import com.accloud.ac_service_android_demo.utils.Pop;
import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.cloudservice.VoidCallback;
import com.accloud.service.ACDeviceMsg;
import com.accloud.service.ACException;
import com.accloud.utils.PreferencesUtils;

/**
 * Created by Xuri on 2015/1/24.
 */
public class Light {
    private static final int OPENLIGHT = 1;
    private static final int CLOSELIGHT = 0;
    private Context context;
    private String subDomain;

    public Light(Context context) {
        this.context = context;
        subDomain = PreferencesUtils.getString(context, "subDomain", Config.SUB_DOMAIN);
    }

    public void openLight(String physicalDeviceId, final VoidCallback voidCallback) {
        /**
         * 通过云端服务往设备发送命令/消息
         *
         * @param subDomain 子域名，如glass（智能眼镜）
         * @param deviceId  设备逻辑id
         * @param msg       具体的消息内容
         *
         * @return 设备返回的监听回调，返回设备的响应消息
         */
        AC.bindMgr().sendToDeviceWithOption(subDomain, physicalDeviceId, getDeviceMsg(OPENLIGHT), AC.LOCAL_FIRST, new PayloadCallback<ACDeviceMsg>() {
            @Override
            public void success(ACDeviceMsg msg) {
                if (parseDeviceMsg(msg)) {
                    Pop.popToast(context, context.getString(R.string.main_aty_openlight_success));
                    if (voidCallback != null) {
                        voidCallback.success();
                    }
                } else {
                    Pop.popToast(context, context.getString(R.string.main_aty_openlight_fail));
                }
            }

            @Override
            public void error(ACException e) {
                Toast.makeText(context, e.getErrorCode() + "-->" + e.getMessage(), Toast.LENGTH_LONG).show();
                if (voidCallback != null) {
                    voidCallback.error(e);
                }
            }
        });
    }

    public void closeLight(String physicalDeviceId, final VoidCallback voidCallback) {
        /**
         * 通过云端服务往设备发送命令/消息
         *
         * @param subDomain 子域名，如glass（智能眼镜）
         * @param deviceId  设备逻辑id
         * @param msg       具体的消息内容
         *
         * @return 设备返回的监听回调，返回设备的响应消息
         */
        AC.bindMgr().sendToDeviceWithOption(subDomain, physicalDeviceId, getDeviceMsg(CLOSELIGHT), AC.LOCAL_FIRST, new PayloadCallback<ACDeviceMsg>() {
            @Override
            public void success(ACDeviceMsg msg) {
                if (parseDeviceMsg(msg)) {
                    Pop.popToast(context, context.getString(R.string.main_aty_closelight_success));
                    if (voidCallback != null) {
                        voidCallback.success();
                    }
                } else {
                    Pop.popToast(context, context.getString(R.string.main_aty_closelight_fail));
                }
            }

            @Override
            public void error(ACException e) {
                Toast.makeText(context, e.getErrorCode() + "-->" + e.getMessage(), Toast.LENGTH_LONG).show();
                if (voidCallback != null) {
                    voidCallback.error(e);
                }
            }
        });
    }

    private ACDeviceMsg getDeviceMsg(int action) {
        return new ACDeviceMsg(Config.LIGHT_MSGCODE, new byte[]{(byte) action, 0, 0, 0});
    }

    private boolean parseDeviceMsg(ACDeviceMsg msg) {
        byte[] bytes = msg.getContent();
        if (bytes != null) return bytes[0] == 1;
        return false;
    }
}
