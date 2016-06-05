package com.mifos.mifosxdroid.injection.component;

import com.mifos.mifosxdroid.login.LoginActivity;
import com.mifos.mifosxdroid.injection.PerActivity;
import com.mifos.mifosxdroid.injection.module.ActivityModule;
import com.mifos.mifosxdroid.online.centerlist.CenterListFragment;
import com.mifos.mifosxdroid.online.clientcharge.ClientChargeFragment;

import dagger.Component;

/**
 * @author Rajan Maurya
 * This component inject dependencies to all Activities across the application
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(LoginActivity loginActivity);

    void inject(CenterListFragment centerListFragment);

    void inject(ClientChargeFragment clientChargeFragment);

}
