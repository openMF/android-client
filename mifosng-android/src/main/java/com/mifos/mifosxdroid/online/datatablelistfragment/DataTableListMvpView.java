package com.mifos.mifosxdroid.online.datatablelistfragment;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.client.Client;

public interface DataTableListMvpView extends MvpView {

    void showMessage(int messageId);

    void showMessage(String message);

    void showClientCreatedSuccessfully(Client client);

    void showWaitingForCheckerApproval(int message);
}
