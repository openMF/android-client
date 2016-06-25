package com.mifos.api.local.databasehelper;

import com.mifos.objects.client.Client;
import com.mifos.objects.client.Page;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Rajan Maurya on 24/06/16.
 */
@Singleton
public class DatabaseHelperClientApi {

    @Inject
    public DatabaseHelperClientApi() {

    }

    public void saveAllClients(Page<Client> clientPage) {

        for (Client client : clientPage.getPageItems()) {
            Client dbClient = new Client();
            dbClient.setId(client.getId());
            dbClient.setAccountNo(client.getAccountNo());
            dbClient.setActive(client.isActive());
            dbClient.save();
        }
    }
}
