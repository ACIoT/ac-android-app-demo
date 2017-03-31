package ablecloud.matrix.app.demo;

import android.app.Application;

import ablecloud.matrix.app.Matrix;

/**
 * Created by wangkun on 24/07/2017.
 */

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Matrix.init(this, BuildConfig.MAJOR_DOMAIN, BuildConfig.MAJOR_DOMAIN_ID, Matrix.TEST_MODE);
    }
}
