package com.mifos.mifosxdroid.injection.component;

import com.mifos.mifosxdroid.injection.PerActivity;
import com.mifos.mifosxdroid.injection.module.ActivityModule;

import dagger.Component;

/**
 * @author Rajan Maurya
 * This component inject dependencies to all Activities across the application
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    //void inject(MainActivity mainActivity);

}
