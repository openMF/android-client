package com.mifos.mifosxdroid.online.generatecollectionsheetfragment;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.group.Center;
import com.mifos.objects.group.CenterWithAssociations;
import com.mifos.objects.group.Group;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.organisation.Staff;

import java.util.List;

/**
 * Created by Rajan Maurya on 18/3/16.
 */
public interface GenerateCollectionSheetMvpView extends MvpView {

    void showofficeList(List<Office> officeList);

    void ResponseError(String s);

    void showStaffList(List<Staff> staffs);

    void showCenterOffice(List<Center> centers);

    void showGroupByOffice(List<Group> groups);

    void showGroupsByCenter(CenterWithAssociations centerWithAssociations);
}
