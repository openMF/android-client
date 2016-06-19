package com.mifos.mifosxdroid;

import com.mifos.objects.client.Client;
import com.mifos.objects.client.Page;

/**
 * Created by Rajan Maurya on 18/6/16.
 */
public class FakeRemoteDataSource {

    private static TestDataFactory mTestDataFactory = new TestDataFactory();


    public static Page<Client> getClientList() {
        return mTestDataFactory.getObjectTypePojo(Page.class, FakeJsonName.CLIENTS_JSON);

    }
}
