package com.mifos.mifosxdroid.online.groupdetails;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.accounts.GroupAccounts;
import com.mifos.objects.group.Group;
import com.mifos.objects.noncore.DataTable;

import java.util.List;


/**
 * Created by Rajan Maurya on 07/06/16.
 */
public interface GroupDetailsMvpView extends MvpView {

    void showGroup(Group group);

    void showGroupsOfClient(GroupAccounts groupAccounts);

    void showClientDataTable(List<DataTable> dataTables);

    void showFetchingError(String s);
}
