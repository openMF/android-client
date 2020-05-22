package com.mifos.mifosxdroid.online.createnewclient;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.organisation.Staff;
import com.mifos.objects.templates.clients.ClientsTemplate;

import java.util.List;

/**
 * Created by Rajan Maurya on 6/6/16.
 */
public interface CreateNewClientMvpView extends MvpView {

    void showUserInterface();

    void showClientTemplate(ClientsTemplate clientsTemplate);

    void showOffices(List<Office> offices);

    void showStaffInOffices(List<Staff> staffs);

    void showClientCreatedSuccessfully(int s);

    void showWaitingForCheckerApproval(int s);

    void showMessage(int message);

    void showMessage(String message);

    void setClientId(Integer id);

    void showProgress(String message);

    void hideProgress();
}
