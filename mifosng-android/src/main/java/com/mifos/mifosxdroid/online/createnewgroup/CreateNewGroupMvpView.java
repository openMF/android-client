package com.mifos.mifosxdroid.online.createnewgroup;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.group.Group;
import com.mifos.objects.organisation.Office;

import java.util.List;

/**
 * Created by Rajan Maurya on 06/06/16.
 */
public interface CreateNewGroupMvpView extends MvpView {

    void showOffices(List<Office> offices);

    void showGroupCreatedSuccessfully(Group group);

    void showFetchingError(String s);
}
