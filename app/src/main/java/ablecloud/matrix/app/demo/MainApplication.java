package ablecloud.matrix.app.demo;

import android.app.Application;

import com.accloud.cloudservice.AC;

/**
 * Created by wangkun on 13/04/2017.
 */

public class MainApplication extends Application {

    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        AC.init(this, BuildConfig.AC_MAJOR_NAME, BuildConfig.AC_MAJOR_ID, BuildConfig.AC_MODE);

        applicationComponent = DaggerApplicationComponent.create();
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}
