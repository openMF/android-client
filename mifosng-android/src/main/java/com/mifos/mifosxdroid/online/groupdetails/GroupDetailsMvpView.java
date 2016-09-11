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

    void showGroupAccounts(GroupAccounts groupAccounts);

    void showGroupDataTable(List<DataTable> dataTables);

    void showFetchingError(int errorMessage);
}
