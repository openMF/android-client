package com.mifos.mifosxdroid.online.createnewclientfragment;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.client.Client;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.templates.clients.ClientsTemplate;

import java.util.List;

/**
 * Created by Rajan Maurya on 18/3/16.
 */
public interface CreateNewClientMvpView extends MvpView {

    void showofficelist(List<Office> offices);

    void ResponseFailed(String s);

    void showClientTemplate(ClientsTemplate clientsTemplate);

    void showCreatedClient(Client client);

    void ErrorToCreateClient(String s);
}
