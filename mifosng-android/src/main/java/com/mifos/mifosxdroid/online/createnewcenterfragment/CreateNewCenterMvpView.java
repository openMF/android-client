package com.mifos.mifosxdroid.online.createnewcenterfragment;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.group.Center;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.organisation.Staff;

import java.util.List;

/**
 * Created by Rajan Maurya on 18/3/16.
 */
public interface CreateNewCenterMvpView extends MvpView {

    void showofficeList(List<Office> offices);

    void showfailedtofetch(String s);

    void showStaffList(List<Staff> staffs);

    void CreateCenter(Center center);

}
