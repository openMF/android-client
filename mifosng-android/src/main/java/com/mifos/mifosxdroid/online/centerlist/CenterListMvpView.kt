package com.mifos.mifosxdroid.online.centerlist

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.group.Center
import com.mifos.objects.group.CenterWithAssociations

/**
 * Created by Rajan Maurya on 5/6/16.
 */
interface CenterListMvpView : MvpView {
    fun showUserInterface()
    fun showCenters(centers: List<Center>)
    fun showMoreCenters(centers: List<Center>)
    fun showEmptyCenters(message: Int)
    fun showMessage(message: Int)
    fun unregisterSwipeAndScrollListener()
    fun showCentersGroupAndMeeting(centerWithAssociations: CenterWithAssociations?, id: Int)
    fun showFetchingError()
}