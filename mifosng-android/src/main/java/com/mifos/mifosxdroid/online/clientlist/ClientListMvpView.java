package com.mifos.mifosxdroid.online.clientlist;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.client.Client;

import java.util.List;

/**
 * Created by Rajan Maurya on 6/6/16.
 */
public interface ClientListMvpView extends MvpView {

    void showUserInterface();

    void showClientList(List<Client>  clients);

    void showLoadMoreClients(List<Client> clients);

    void showEmptyClientList(int message);

    void unregisterSwipeAndScrollListener();

    void showMessage(int message);

    void showError();

}
