package com.mifos.mifosxdroid.online.generatecollectionsheet

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.collectionsheet.CenterDetail
import com.mifos.objects.collectionsheet.CollectionSheetResponse
import com.mifos.objects.group.Center
import com.mifos.objects.group.CenterWithAssociations
import com.mifos.objects.group.Group
import com.mifos.objects.organisation.Office
import com.mifos.objects.organisation.Staff

/**
 * Created by Rajan Maurya on 06/06/16.
 */
interface GenerateCollectionSheetMvpView : MvpView {
    fun showOffices(offices: List<Office>)
    fun showStaffInOffice(staffs: List<Staff>, officeId: Int)
    fun showCentersInOffice(centers: List<Center>)
    fun showGroupsInOffice(groups: List<Group>)
    fun showGroupByCenter(centerWithAssociations: CenterWithAssociations)
    fun showError(s: String?)
    fun onCenterLoadSuccess(centerDetails: List<CenterDetail>)
    fun showProductive(sheet: CollectionSheetResponse)
    fun showCollection(sheet: CollectionSheetResponse)
}