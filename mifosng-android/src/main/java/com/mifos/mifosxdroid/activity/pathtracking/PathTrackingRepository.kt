package com.mifos.mifosxdroid.activity.pathtracking

import com.mifos.core.objects.user.UserLocation
import rx.Observable

/**
 * Created by Aditya Gupta on 06/08/23.
 */
interface PathTrackingRepository {

    fun getUserPathTracking(userId: Int): Observable<List<UserLocation>>

}