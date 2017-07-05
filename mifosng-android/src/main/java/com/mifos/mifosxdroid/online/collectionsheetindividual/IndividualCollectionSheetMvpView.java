package com.mifos.mifosxdroid.online.collectionsheetindividual;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.collectionsheet.IndividualCollectionSheet;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.organisation.Staff;

import java.util.List;

/**
 * Created by Tarun on 05-07-2017.
 */

interface IndividualCollectionSheetMvpView extends MvpView {

    void showSheet(IndividualCollectionSheet sheet);

    void showSuccess();

    void showError(String error);

    void setOfficeSpinner(List<Office> officeList);

    void setStaffSpinner(List<Staff> staffList);

    void showNoSheetFound();
}
