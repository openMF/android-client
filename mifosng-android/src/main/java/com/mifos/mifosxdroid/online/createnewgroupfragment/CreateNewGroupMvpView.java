package com.mifos.mifosxdroid.online.createnewgroupfragment;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.group.GroupCreationResponse;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.organisation.Staff;

import java.util.List;

/**
 * Created by Rajan Maurya on 18/3/16.
 */
public interface CreateNewGroupMvpView extends MvpView {

    void showOfficeList(List<Office> offices);

    void ResponseError(String s);

    void showStaffOfficeList(List<Staff> staffs);

    void showGroupCreationResponse(GroupCreationResponse groupCreationResponse);

    void GroupCreationError(String s);
}
