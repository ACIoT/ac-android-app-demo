package com.accloud.ac_service_android_demo.controller;

import android.content.Context;
import android.widget.Toast;

import com.accloud.ac_service_android_demo.R;
import com.accloud.ac_service_android_demo.activity.ConfigurationActivity;
import com.accloud.ac_service_android_demo.application.MainApplication;
import com.accloud.ac_service_android_demo.config.Config;
import com.accloud.ac_service_android_demo.utils.Pop;
import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACDeviceMsg;
import com.accloud.service.ACException;
import com.accloud.utils.PreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

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
        subDomain = PreferencesUtils.getString(MainApplication.getInstance(), "subDomain", Config.SUBDOMAIN);
    }

    public void openLight(String physicalDeviceId) {
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
                if (parseDeviceMsg(msg))
                    Pop.popToast(context, context.getString(R.string.main_aty_openlight_success));
                else
                    Pop.popToast(context, context.getString(R.string.main_aty_openlight_fail));
            }

            @Override
            public void error(ACException e) {
                Toast.makeText(context, e.getErrorCode() + "-->" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void closeLight(String physicalDeviceId) {
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
                if (parseDeviceMsg(msg))
                    Pop.popToast(context, context.getString(R.string.main_aty_closelight_success));
                else
                    Pop.popToast(context, context.getString(R.string.main_aty_closelight_fail));
            }

            @Override
            public void error(ACException e) {
                Toast.makeText(context, e.getErrorCode() + "-->" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private ACDeviceMsg getDeviceMsg(int action) {
        //注意：实际开发的时候请选择其中的一种消息格式即可
        switch (getFormatType()) {
            case ConfigurationActivity.BINARY:
                return new ACDeviceMsg(Config.LIGHT_MSGCODE, new byte[]{(byte) action, 0, 0, 0});
            case ConfigurationActivity.JSON:
                JSONObject object = new JSONObject();
                try {
                    object.put("switch", action);
                } catch (JSONException e) {
                }
                return new ACDeviceMsg(70, object.toString().getBytes());
        }
        return null;
    }

    private boolean parseDeviceMsg(ACDeviceMsg msg) {
        //注意：实际开发的时候请选择其中的一种消息格式即可
        switch (getFormatType()) {
            case ConfigurationActivity.BINARY:
                byte[] bytes = msg.getContent();
                if (bytes != null)
                    return bytes[0] == 1;
            case ConfigurationActivity.JSON:
                try {
                    JSONObject object = new JSONObject(new String(msg.getContent()));
                    return object.optBoolean("result");
                } catch (Exception e) {
                }
        }
        return false;
    }

    //注意：实际开发消息格式为已知，无需选择
    public int getFormatType() {
        return PreferencesUtils.getInt(context, "formatType", ConfigurationActivity.BINARY);
    }
}
