package com.accloud.ac_service_android_demo.application;

import android.app.Application;

import com.accloud.ac_service_android_demo.config.Config;
import com.accloud.cloudservice.AC;
import com.accloud.utils.PreferencesUtils;

/**
 * Created by Xuri on 2015/1/17.
 */
public class MainApplication extends Application {
    private static MainApplication mInstance;
    private String domain;
    private long domainId;

    @Override
    public void onCreate() {
        super.onCreate();

        //读取本地存储的配置信息,开发者在实际开发中直接调用AC.init()方法即可
        domain = PreferencesUtils.getString(this, "domain", Config.MAJORDOAMIN);
        domainId = PreferencesUtils.getLong(this, "domainId", Config.MAJORDOMAINID);
        String routerAddr = PreferencesUtils.getString(this, "routerAddr", "");
        if (!routerAddr.equals("")) {
            AC.setRouterAddress(routerAddr);
        }

        AC.init(this, domain, domainId, AC.TEST_MODE);

        mInstance = this;
    }

    public static MainApplication getInstance() {
        return mInstance;
    }
}
