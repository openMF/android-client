package com.mifos.mifosxdroid.online.clientdetailsfragment;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.accounts.ClientAccounts;
import com.mifos.objects.client.Client;
import com.mifos.objects.noncore.DataTable;

import java.util.List;

/**
 * Created by Rajan Maurya on 17/3/16.
 */
public interface ClientDetailsMvpView extends MvpView {

    void showClientDetailsProgressBar(boolean status);

    void showSuccessfullRequest(String done);

    void showError(String error);

    void showsuccessfullimageuploaded(String imagepath);

    void showfailedtouploadedimage();

    void showclientdetails(Client client);

    void showaccountdetails(ClientAccounts clientAccounts);

    void showclientdatatable(List<DataTable> dataTables);

}
