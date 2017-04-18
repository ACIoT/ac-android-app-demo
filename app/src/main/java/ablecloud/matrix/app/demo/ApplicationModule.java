package ablecloud.matrix.app.demo;

import javax.inject.Singleton;

import ablecloud.matrix.app.DeviceManager;
import dagger.Module;
import dagger.Provides;

/**
 * Created by wangkun on 18/04/2017.
 */

@Module
public class ApplicationModule {
    @Provides
    @Singleton
    public DeviceManager deviceManager() {
        return new DeviceManager();
    }
}
