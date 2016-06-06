package com.mifos.mifosxdroid.online.generatecollectionsheet;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.group.Center;
import com.mifos.objects.group.CenterWithAssociations;
import com.mifos.objects.group.Group;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.organisation.Staff;

import java.util.List;

/**
 * Created by Rajan Maurya on 06/06/16.
 */
public interface GenerateCollectionSheetMvpView extends MvpView {

    void showOffices(List<Office> offices);

    void showStaffInOffice(List<Staff> staffs, int officeId);

    void showCentersInOffice(List<Center> centers);

    void showGroupsInOffice(List<Group> groups);

    void showGroupByCenter(CenterWithAssociations centerWithAssociations);

    void showFetchingError(String s);
}
