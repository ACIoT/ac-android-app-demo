package com.accloud.ac_service_android_demo.application;

import android.app.Application;

import com.accloud.ac_service_android_demo.config.Config;
import com.accloud.cloudservice.AC;

/**
 * Created by Xuri on 2015/1/17.
 */
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AC.init(this, Config.MAJOR_DOAMIN, Config.MAJOR_DOMAIN_ID, AC.TEST_MODE);
    }

}
