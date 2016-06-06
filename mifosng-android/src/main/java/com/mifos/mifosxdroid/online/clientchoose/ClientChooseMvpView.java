package com.mifos.mifosxdroid.online.clientchoose;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.Page;

/**
 * Created by Rajan Maurya on 06/06/16.
 */
public interface ClientChooseMvpView extends MvpView {

    void showClientList(Page<Client> clientPage);

    void showMoreClientList(Page<Client> clientPage);

    void showFetchingError(String s);

}
