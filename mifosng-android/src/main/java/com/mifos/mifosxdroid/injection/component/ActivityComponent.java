package com.mifos.mifosxdroid.injection.component;

import com.mifos.mifosxdroid.login.LoginActivity;
import com.mifos.mifosxdroid.injection.PerActivity;
import com.mifos.mifosxdroid.injection.module.ActivityModule;
import com.mifos.mifosxdroid.online.centerlist.CenterListFragment;
import com.mifos.mifosxdroid.online.clientcharge.ClientChargeFragment;
import com.mifos.mifosxdroid.online.clientchoose.ClientChooseFragment;
import com.mifos.mifosxdroid.online.clientidentifiers.ClientIdentifiersFragment;
import com.mifos.mifosxdroid.online.clientlist.ClientListFragment;
import com.mifos.mifosxdroid.online.clientsearch.ClientSearchFragment;
import com.mifos.mifosxdroid.online.createnewcenter.CreateNewCenterFragment;
import com.mifos.mifosxdroid.online.createnewclient.CreateNewClientFragment;
import com.mifos.mifosxdroid.online.createnewgroup.CreateNewGroupFragment;
import com.mifos.mifosxdroid.online.documentlist.DocumentListFragment;
import com.mifos.mifosxdroid.online.generatecollectionsheet.GenerateCollectionSheetFragment;
import com.mifos.mifosxdroid.online.grouplist.GroupListFragment;

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

    void inject(ClientListFragment clientListFragment);

    void inject(ClientChooseFragment clientChooseFragment);

    void inject(ClientIdentifiersFragment clientIdentifiersFragment);

    void inject(ClientSearchFragment clientSearchFragment);

    void inject(DocumentListFragment documentListFragment);

    void inject(GroupListFragment groupListFragment);

    void inject(GenerateCollectionSheetFragment generateCollectionSheetFragment);

    void inject(CreateNewCenterFragment createNewCenterFragment);

    void inject(CreateNewGroupFragment createNewGroupFragment);

    void inject(CreateNewClientFragment createNewClientFragment);

}
