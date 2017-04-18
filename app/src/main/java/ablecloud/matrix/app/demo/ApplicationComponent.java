package ablecloud.matrix.app.demo;

import javax.inject.Singleton;

import ablecloud.matrix.app.DeviceManager;
import dagger.Component;

/**
 * Created by wangkun on 14/02/2017.
 */

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    DeviceManager deviceManager();
}
