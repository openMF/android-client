package com.mifos.mifosxdroid.online.collectionsheetindividual

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.collectionsheet.IndividualCollectionSheet
import com.mifos.objects.organisation.Office
import com.mifos.objects.organisation.Staff

/**
 * Created by Tarun on 05-07-2017.
 */
interface IndividualCollectionSheetMvpView : MvpView {
    fun showSheet(sheet: IndividualCollectionSheet?)
    fun showSuccess()
    fun showError(error: String?)
    fun setOfficeSpinner(officeList: List<Office>?)
    fun setStaffSpinner(staffList: List<Staff>?)
    fun showNoSheetFound()
}