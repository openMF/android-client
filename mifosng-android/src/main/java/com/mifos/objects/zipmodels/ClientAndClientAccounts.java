package com.mifos.objects.zipmodels;

import com.mifos.objects.accounts.ClientAccounts;
import com.mifos.objects.client.Client;

/**
 * Model for Observable.zip. This Model used to combine the Client and ClientAccount in response
 * of RxAndroid.
 * Created by Rajan Maurya on 01/07/16.
 */
public class ClientAndClientAccounts {

    Client client;
    ClientAccounts clientAccounts;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public ClientAccounts getClientAccounts() {
        return clientAccounts;
    }

    public void setClientAccounts(ClientAccounts clientAccounts) {
        this.clientAccounts = clientAccounts;
    }
}
