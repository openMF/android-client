package com.mifos.mifosxdroid.online.createnewclient;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.client.Client;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.organisation.Staff;
import com.mifos.objects.templates.clients.ClientsTemplate;

import java.util.List;

/**
 * Created by Rajan Maurya on 6/6/16.
 */
public interface CreateNewClientMvpView extends MvpView {

    void showClientTemplate(ClientsTemplate clientsTemplate);

    void showOffices(List<Office> offices);

    void showStaffInOffices(List<Staff> staffs);

    void showClientCreatedSuccessfully(Client client, String s);

    void showFetchingError(String s);
}
