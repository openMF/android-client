/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.clientlistfragment;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.Page;

/**
 * Created by Rajan Maurya on 17/3/16.
 */
public interface ClientListMvpView extends MvpView {

    void showClientList(Page<Client> clientPage);

    void showErrorFetchingList();

    void showMoreClientList(Page<Client> clientPage);

    void showprogressbar(boolean status);

}
