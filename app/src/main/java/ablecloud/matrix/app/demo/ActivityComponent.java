package ablecloud.matrix.app.demo;

import ablecloud.support.annotation.PerActivity;
import dagger.Component;

/**
 * Created by wangkun on 25/02/2017.
 */

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {
    void inject(BaseActivity baseActivity);

    void inject(BaseFragment baseFragment);
}
