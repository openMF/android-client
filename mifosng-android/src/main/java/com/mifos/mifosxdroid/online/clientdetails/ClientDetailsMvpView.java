package com.mifos.mifosxdroid.online.clientdetails;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.accounts.ClientAccounts;
import com.mifos.objects.client.Client;
import com.mifos.objects.noncore.DataTable;

import java.util.List;

import retrofit.client.Response;

/**
 * Created by Rajan Maurya on 07/06/16.
 */
public interface ClientDetailsMvpView extends MvpView {

    void showClientDataTable(List<DataTable> dataTables);

    void showClientInformation(Client client);

    void showUploadImageSuccessfully(Response response, String imagePath);

    void showUploadImageFailed(String s);

    void showUploadImageProgressbar(boolean b);

    void showClientImageDeletedSuccessfully();

    void showClientAccount(ClientAccounts clientAccounts);

    void showFetchingError(String s);
}
