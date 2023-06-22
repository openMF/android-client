package com.mifos.mifosxdroid.activity.pathtracking

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.user.UserLocation

/**
 * Created by Rajan Maurya on 24/01/17.
 */
interface PathTrackingMvpView : MvpView {
    fun showUserInterface()
    fun showPathTracking(userLocations: List<UserLocation>)
    fun showEmptyPathTracking()
    fun showError()
}