package com.mifos.repositories

import com.mifos.objects.user.UserLocation
import rx.Observable

/**
 * Created by Aditya Gupta on 06/08/23.
 */
interface PathTrackingRepository {

    fun getUserPathTracking(userId: Int): Observable<List<UserLocation>>

}