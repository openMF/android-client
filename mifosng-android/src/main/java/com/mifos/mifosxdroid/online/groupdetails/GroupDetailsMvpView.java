package com.mifos.mifosxdroid.online.groupdetails;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.accounts.GroupAccounts;
import com.mifos.objects.client.Client;
import com.mifos.objects.group.Group;

import java.util.List;


/**
 * Created by Rajan Maurya on 07/06/16.
 */
public interface GroupDetailsMvpView extends MvpView {

    void showGroup(Group group);

    void showGroupClients(List<Client> clients);

    void showGroupAccounts(GroupAccounts groupAccounts);

    void showFetchingError(int errorMessage);
}
