package ablecloud.matrix.app.demo;

import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACDeviceMsg;
import com.accloud.service.ACException;

import ablecloud.matrix.app.DeviceApi;

/**
 * Created by wangkun on 19/04/2017.
 */

public class LightDeviceApi extends DeviceApi {
    private static final int MSG_CODE_LIGHT = 68;
    private static final byte[] CMD_TURN_OFF = {0x00, 0, 0, 0};
    private static final byte[] CMD_TURN_ON = {0x01, 0, 0, 0};
    private static final byte RESULT_SUCCESS = 0x01;

    private static LightDeviceApi sInstance = new LightDeviceApi(DeviceControl.demo.name(), AC.LOCAL_FIRST);

    public LightDeviceApi(String domain, int option) {
        super(domain, option);
    }

    public static void turnPower(final String physicalDeviceId, final boolean powerOn, final PayloadCallback<Boolean> callback) {
        sInstance.send(physicalDeviceId, new ACDeviceMsg(MSG_CODE_LIGHT, powerOn ? CMD_TURN_ON : CMD_TURN_OFF), new PayloadCallback<ACDeviceMsg>() {
            @Override
            public void success(ACDeviceMsg acDeviceMsg) {
                byte[] content = acDeviceMsg.getContent();
                if (content != null && content.length > 0) {
                    callback.success(content[0] == RESULT_SUCCESS);
                } else {
                    callback.error(new ACException(ACException.INVALID_PAYLOAD, "Empty payload"));
                }
            }

            @Override
            public void error(ACException e) {
                callback.error(e);
            }
        });
    }
}
