package com.mifos.mifosxdroid.online.clientlist;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.Page;

/**
 * Created by Rajan Maurya on 6/6/16.
 */
public interface ClientListMvpView extends MvpView {


    void showClientList(Page<Client> clientPage);

    void showErrorFetchingClients();

}
