package com.mifos.mifosxdroid.online.kycdetails;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.client.Client;

public interface CreateNewKycMvpView extends MvpView {

    void showKycInformation(Client client);

    void showUserInterface();
}
