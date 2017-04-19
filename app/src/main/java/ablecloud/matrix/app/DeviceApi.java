package ablecloud.matrix.app;

import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACDeviceMsg;

/**
 * Created by wangkun on 19/04/2017.
 */

public class DeviceApi {
    private final String domain;
    private final int option;

    public DeviceApi(String domain, int option) {
        this.domain = domain;
        this.option = option;
    }

    public void send(String physicalDeviceId, ACDeviceMsg msg, final PayloadCallback<ACDeviceMsg> callback) {
        AC.bindMgr().sendToDeviceWithOption(domain, physicalDeviceId, msg, option, callback);
    }
}
