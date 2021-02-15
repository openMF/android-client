package com.mifos.mifosxdroid.online.centerdetails

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.group.CenterInfo
import com.mifos.objects.group.CenterWithAssociations

/**
 * Created by Rajan Maurya on 05/02/17.
 */
interface CenterDetailsMvpView : MvpView {
    override fun showProgressbar(show: Boolean)
    fun showCenterDetails(centerWithAssociations: CenterWithAssociations?)
    fun showMeetingDetails(centerWithAssociations: CenterWithAssociations?)
    fun showSummaryInfo(centerInfos: List<CenterInfo?>?)
    fun showErrorMessage(message: Int)
}